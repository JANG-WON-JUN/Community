package com.jwj.community.domain.service.member;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.board.Comment;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.web.exception.CannotEditBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Locale.getDefault;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCheckService {

    private final MessageSource messageSource;

    public boolean isBoardEditable(Board board, Member loggedInMember){
        if(!board.getMember().getId().equals(loggedInMember.getId())){
            throw new CannotEditBoard(messageSource.getMessage("error.cannotEditBoard", null, getDefault()));
        }
        return true;
    }

    public boolean isCommentEditable(Comment comment, Member loggedInMember){
        if(!comment.getMember().getId().equals(loggedInMember.getId())){
            throw new CannotEditBoard(messageSource.getMessage("error.cannotEditComment", null, getDefault()));
        }
        return true;
    }
}
