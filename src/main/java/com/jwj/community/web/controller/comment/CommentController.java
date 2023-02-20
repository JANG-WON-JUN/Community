package com.jwj.community.web.controller.comment;

import com.jwj.community.domain.service.comment.CommentService;
import com.jwj.community.web.annotation.LoginMember;
import com.jwj.community.web.common.result.Result;
import com.jwj.community.web.dto.comment.request.CommentCreate;
import com.jwj.community.web.dto.comment.request.CommentEdit;
import com.jwj.community.web.dto.member.login.LoggedInMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/member/comment")
    public ResponseEntity<Result<Long>> createComment(@Valid @RequestBody CommentCreate commentCreate,
                                                      @LoginMember LoggedInMember loggedInMember){
        Result<Long> result = Result.<Long>builder()
                .data(commentService.createComment(commentCreate.toEntity(), loggedInMember.getEmail()))
                .build();

        return ok(result);
    }

    @PatchMapping("/api/member/comment")
    public ResponseEntity<Result<Long>> editComment(@Valid @RequestBody CommentEdit commentEdit,
                                                    @LoginMember LoggedInMember loggedInMember){
        Result<Long> result = Result.<Long>builder()
                .data(commentService.editComment(commentEdit.toEntity(), loggedInMember.getEmail()))
                .build();

        return ok(result);
    }

    @DeleteMapping("/api/member/comment/{id}")
    public void deleteComment(@PathVariable Long id, @LoginMember LoggedInMember loggedInMember){
        commentService.deleteComment(id, loggedInMember.getEmail());
    }
}
