package com.jwj.community.web.controller.board;

import org.springframework.web.bind.annotation.*;

@RestController
public class BoardController {

    @GetMapping("/api/board")
    public void boards(){
        // 글 목록 조회하기
    }

    @GetMapping("/api/board/{id}")
    public void board(Long id){
        // 글 1개 조회하기
    }

    @PostMapping("/api/member/board")
    public void save(){
        // 작성한 글을 저장하기
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