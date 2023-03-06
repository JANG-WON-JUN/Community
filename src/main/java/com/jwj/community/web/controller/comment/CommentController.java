package com.jwj.community.web.controller.comment;

import com.jwj.community.domain.entity.board.Comment;
import com.jwj.community.domain.service.comment.CommentService;
import com.jwj.community.web.annotation.LoginMember;
import com.jwj.community.web.common.paging.request.CommentSearchCondition;
import com.jwj.community.web.common.result.ListResult;
import com.jwj.community.web.common.result.Result;
import com.jwj.community.web.dto.comment.request.CommentCreate;
import com.jwj.community.web.dto.comment.request.CommentEdit;
import com.jwj.community.web.dto.comment.response.CommentView;
import com.jwj.community.web.dto.member.login.LoggedInMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/member/comment")
    public ResponseEntity<ListResult<CommentView>> getComments(CommentSearchCondition condition){
        Page<Comment> commentPage = commentService.getComments(condition);

        List<CommentView> comments = commentPage.stream()
                .map(comment -> CommentView.builder().comment(comment).build())
                .collect(toList());

        ListResult<CommentView> resultList = ListResult.<CommentView>builder()
                .list(comments)
                .page(commentPage)
                .build();

        return ok(resultList);
    }

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
