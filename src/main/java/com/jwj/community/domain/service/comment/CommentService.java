package com.jwj.community.domain.service.comment;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.board.Comment;
import com.jwj.community.domain.repository.comment.CommentRepository;
import com.jwj.community.domain.service.board.BoardService;
import com.jwj.community.domain.service.member.MemberCheckService;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.exception.CommentNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Locale.getDefault;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final MemberService memberService;
    private final BoardService boardService;
    private final MemberCheckService memberCheckService;
    private final CommentRepository commentRepository;
    private final MessageSource messageSource;

    public Long createComment(Comment comment, String email){
        Board savedBoard =  boardService.getBoard(comment.getBoard().getId());

        if(comment.getParent() == null){
            Integer maxCommentGroup = commentRepository.getMaxCommentGroup(savedBoard);

            comment.setCommentGroup(maxCommentGroup + 1);
        }else{
            Integer parentCommentGroup = comment.getParent().getCommentGroup();
            Integer maxCommentOrder = commentRepository.getMaxCommentOrder(savedBoard, parentCommentGroup);

            comment.setCommentGroup(parentCommentGroup);
            comment.setCommentOrder(maxCommentOrder + 1);
        }
        Comment savedComment = commentRepository.save(comment);
        savedComment.setWriter(memberService.findByEmail(email));

        return savedComment.getBoard().getId();
    }

    public List<Comment> getComments(Long boardId){
        return commentRepository.getComments(boardService.getBoard(boardId));
    }

    public Long editComment(Comment comment, String email){
        Comment savedComment = findById(comment.getId());
        isCommentEditable(savedComment, email);
        savedComment.edit(comment);

        return savedComment.getBoard().getId();
    }

    public void deleteComment(Long id, String email){
        isCommentEditable(findById(id), email);
        commentRepository.deleteById(id);
    }

    private Comment findById(Long id){
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFound(messageSource.getMessage("error.noComment", null, getDefault())));
    }

    private boolean isCommentEditable(Comment comment, String email){
        return memberCheckService.isCommentEditable(comment, memberService.findByEmail(email));
    }
}
