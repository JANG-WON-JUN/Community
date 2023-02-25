package com.jwj.community.web.controller.board;

import com.jwj.community.domain.service.board.BoardTypeService;
import com.jwj.community.web.common.result.ListResult;
import com.jwj.community.web.dto.board.response.BoardTypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
public class BoardTypeController {

    private final BoardTypeService boardTypeService;

    @GetMapping("/api/boardType")
    public ResponseEntity<ListResult<BoardTypeResponse>> boardTypes(){
        List<BoardTypeResponse> boardTypes = boardTypeService.getBoardTypes().stream()
                .map(boardType -> BoardTypeResponse.builder().boardType(boardType).build())
                .collect(toList());

        ListResult<BoardTypeResponse> resultList = ListResult.<BoardTypeResponse>builder()
                .list(boardTypes)
                .build();

        return ok(resultList);
    }
}
