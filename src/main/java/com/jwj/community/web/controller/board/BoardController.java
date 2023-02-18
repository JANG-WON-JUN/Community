package com.jwj.community.web.controller.board;

import com.jwj.community.domain.service.board.BoardService;
import com.jwj.community.web.annotation.LoginMember;
import com.jwj.community.web.common.result.Result;
import com.jwj.community.web.dto.board.request.BoardCreate;
import com.jwj.community.web.dto.member.login.LoggedInMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/api/board")
    public void boards(){
        // 글 목록 조회하기
    }

    @GetMapping("/api/board/{id}")
    public void board(Long id){
        // 글 1개 조회하기
    }

    @PostMapping("/api/member/board")
    public ResponseEntity<Result<Long>> save(@Valid @RequestBody BoardCreate boardCreate, @LoginMember LoggedInMember loggedInMember){
        Result<Long> result = Result.<Long>builder()
                .data(boardService.createBoard(boardCreate.toEntity(), loggedInMember.getEmail()))
                .build();

        return ok(result);
    }

    @PostMapping("/api/member/board/tmp")
    public void tmpSave(){
        // 작성한 글을 임시 저장하기
    }
    
    @PostMapping("/api/member/board/tmp{id}")
    public void tmpBoard(){
        // 임시저장한 글을 불러오기
    }

    @PatchMapping("/api/member/board/{id}")
    public void modify(){
        // 임시저장한 글을 불러오기
    }

    @DeleteMapping("/api/member/board/{id}")
    public void delete(){
        // 임시저장한 글을 불러오기
    }
}
