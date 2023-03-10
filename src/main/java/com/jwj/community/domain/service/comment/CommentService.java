package com.jwj.community.domain.service.comment;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.board.Comment;
import com.jwj.community.domain.repository.comment.CommentRepository;
import com.jwj.community.domain.service.board.BoardService;
import com.jwj.community.domain.service.member.MemberCheckService;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.common.paging.request.CommentSearchCondition;
import com.jwj.community.web.exception.CommentNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            commentRepository.save(comment);
        }else{
            // child의 경우 parent를 조회하여 영속성 컨텍스트에 포함하고
            // Cascade.Persist를 선언해주었으므로 객체의 연관관계만 매핑하면
            // 영속성 컨텍스트가 commentRepository.save(child)를 해준다.
            Comment parent = findById(comment.getParent().getId());
            Integer maxCommentOrder = commentRepository.getMaxCommentOrder(savedBoard, parent.getCommentGroup());

            comment.setParent(parent);
            comment.setCommentGroup(parent.getCommentGroup());
            comment.setCommentOrder(maxCommentOrder + 1);

            parent.addChild(comment);
        }

        comment.setWriter(memberService.findByEmail(email));
        savedBoard.addComment(comment);

        return comment.getBoard().getId();
    }

    public Page<Comment> getComments(CommentSearchCondition condition){
        return commentRepository.getComments(condition, boardService.getBoard(condition.getBoardId()));
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
