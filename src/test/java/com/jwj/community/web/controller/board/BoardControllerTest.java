package com.jwj.community.web.controller.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.config.security.utils.code.JwtTokenFactory;
import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.service.board.BoardService;
import com.jwj.community.domain.service.comment.CommentService;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.annotation.ControllerTest;
import com.jwj.community.web.common.paging.request.BoardSearchCondition;
import com.jwj.community.web.dto.board.request.BoardCreate;
import com.jwj.community.web.dto.board.request.BoardEdit;
import com.jwj.community.web.dto.comment.request.CommentCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.exception.BoardNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static com.jwj.community.domain.enums.BoardTypes.DAILY;
import static com.jwj.community.domain.enums.Sex.MALE;
import static com.jwj.community.web.common.consts.JwtConst.AUTHORIZATION;
import static java.util.Locale.getDefault;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MemberService memberService;

    @Autowired
    BoardService boardService;

    @Autowired
    CommentService commentService;

    @Autowired
    MessageSource messageSource;

    private final String TEST_EMAIL = "admin@google.com";
    private final String TEST_ANOTHER_EMAIL = "member@google.com";
    private final String TEST_TITLE = "글 제목";
    private final String TEST_CONTENT = "글 내용";
    private final String TEST_NICKNAME = "어드민 닉네임";
    private final String TEST_ANOTHER_NICKNAME = "회원 닉네임";
    private final JwtTokenFactory jwtTokenFactory = new JwtTokenFactory();

    @BeforeEach
    public void setup(){
        // 회원 등록
        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .name("어드민")
                .nickname(TEST_NICKNAME)
                .password("1234")
                .year(2023)
                .month(1)
                .day(28)
                .sex(MALE)
                .build();

        MemberCreate anotherMemberCreate = MemberCreate.builder()
                .email(TEST_ANOTHER_EMAIL)
                .name("회원")
                .nickname(TEST_ANOTHER_NICKNAME)
                .password("1234")
                .year(2023)
                .month(1)
                .day(28)
                .sex(MALE)
                .build();

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPasswordEntity());
        memberService.createMember(anotherMemberCreate.toEntity(), anotherMemberCreate.getPasswordEntity());
    }

    @Test
    @DisplayName("글 등록하기 - 모든정보 입력 시 성공")
    void createWithAllInfoTest() throws Exception{
        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .tempSave(false)
                .boardType(DAILY)
                .build();

        mockMvc.perform(post("/api/member/board")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(boardCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("글 등록하기 - 글 제목은 필수입력")
    void requiredTitleTest() throws Exception{
        BoardCreate boardCreate = BoardCreate.builder()
                .content(TEST_CONTENT)
                .tempSave(false)
                .boardType(DAILY)
                .build();

        mockMvc.perform(post("/api/member/board")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(boardCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("title"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.title", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("글 등록하기 - 글 내용은 빈값허용")
    void allowBlankContentTest() throws Exception{
        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content("")
                .tempSave(false)
                .boardType(DAILY)
                .build();

        mockMvc.perform(post("/api/member/board")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(boardCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("글 등록하기 - 글 내용은 null 허용")
    void allowNullContentTest() throws Exception{
        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content(null)
                .tempSave(false)
                .boardType(DAILY)
                .build();

        mockMvc.perform(post("/api/member/board")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(boardCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("글 등록하기 - 게시판 타입은 필수입력")
    void requiredBoardTypeTest() throws Exception{
        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content(null)
                .tempSave(false)
                .boardType(null)
                .build();

        mockMvc.perform(post("/api/member/board")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(boardCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("boardType"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.boardType", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("글 리스트 조회하기")
    void boardListTest() throws Exception{
        IntStream.rangeClosed(1, 7).forEach(i -> createBoard(TEST_EMAIL));

        mockMvc.perform(get("/api/board")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(BoardSearchCondition.builder().build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size").value(7))
                .andExpect(jsonPath("list[0].id").value(7))
                .andExpect(jsonPath("list[1].id").value(6))
                .andExpect(jsonPath("list[2].id").value(5))
                .andDo(print());
    }

    @Test
    @DisplayName("글 리스트 조회하기 - 조회순 정렬조건 추가")
    void boardListWithViewsOrderTest() throws Exception{
        IntStream.rangeClosed(1, 7).forEach(i -> createBoard(TEST_EMAIL));

        Map<String, String> condition = new HashMap<>();
        condition.put("searchOrder", "v");

        mockMvc.perform(get("/api/board")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(condition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size").value(7))
                .andExpect(jsonPath("list[0].id").value(7))
                .andExpect(jsonPath("list[1].id").value(6))
                .andExpect(jsonPath("list[2].id").value(5))
                .andDo(print());
    }

    @Test
    @DisplayName("글 리스트 조회하기 - 제목 조회조건 추가, 조회결과 없음")
    void boardListWithSearchTitleConditionTest() throws Exception{
        IntStream.rangeClosed(1, 7).forEach(i -> createBoard(TEST_EMAIL));

        MultiValueMap<String, String> condition = new LinkedMultiValueMap<>();
        condition.add("searchType", "t");
        condition.add("keyword", "없는 키워드");

        mockMvc.perform(get("/api/board")
                .contentType(APPLICATION_JSON_VALUE)
                .params(condition))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("글 리스트 조회하기 - 제목 조회조건 추가, 조회결과 있음")
    void boardListWithSearchTitleConditionTest2() throws Exception{
        IntStream.rangeClosed(1, 7).forEach(i -> createBoard(TEST_EMAIL));

        MultiValueMap<String, String> condition = new LinkedMultiValueMap<>();
        condition.add("searchType", "t");
        condition.add("keyword", TEST_TITLE);

        mockMvc.perform(get("/api/board")
                .contentType(APPLICATION_JSON_VALUE)
                .params(condition))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size").value(7))
                .andDo(print());
    }

    @Test
    @DisplayName("글 리스트 조회하기 - 작성자 닉네임 조회조건 추가, 조회결과 있음")
    void boardListWithSearchWriterConditionTest1() throws Exception{
        IntStream.rangeClosed(1, 7).forEach(i -> createBoard(TEST_EMAIL));

        Map<String, String> condition = new HashMap<>();
        condition.put("searchType", "w");
        condition.put("keyword", TEST_NICKNAME);

        mockMvc.perform(get("/api/board")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(condition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size").value(7))
                .andDo(print());
    }

    @Test
    @DisplayName("글 리스트 조회하기 - 작성자 닉네임 조회조건 추가, 조회결과 없음")
    void boardListWithSearchWriterConditionTest2() throws Exception{
        IntStream.rangeClosed(1, 7).forEach(i -> createBoard(TEST_EMAIL));

        MultiValueMap<String, String> condition = new LinkedMultiValueMap<>();
        condition.add("searchType", "w");
        condition.add("keyword", TEST_ANOTHER_NICKNAME);

        mockMvc.perform(get("/api/board")
                .contentType(APPLICATION_JSON_VALUE)
                .params(condition))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("글 리스트 조회하기 - 검색조건 없을 때 전체조회")
    void boardListWithNoSearchConditionTest() throws Exception{
        IntStream.rangeClosed(1, 7).forEach(i -> createBoard(TEST_EMAIL));

        mockMvc.perform(get("/api/board")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(new HashMap<String, String>())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size").value(7))
                .andDo(print());
    }

    @Test
    @DisplayName("글 리스트 조회하기 - 첫 페이지 결과가 있을 때")
    void boardListWithPagingSearchConditionTest1() throws Exception{
        IntStream.rangeClosed(1, 7).forEach(i -> createBoard(TEST_EMAIL));

        HashMap<String, String> condition = new HashMap<>();
        condition.put("page", "0");
        condition.put("size", "10");

        mockMvc.perform(get("/api/board")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(condition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size").value(7))
                .andDo(print());
    }

    @Test
    @DisplayName("글 리스트 조회하기 - 두번째 페이지 결과가 없을 때")
    void boardListWithPagingSearchConditionTest2() throws Exception{
        IntStream.rangeClosed(1, 7).forEach(i -> createBoard(TEST_EMAIL));

        MultiValueMap<String, String> condition = new LinkedMultiValueMap<>();
        condition.add("page", "1");
        condition.add("size", "10");

        mockMvc.perform(get("/api/board")
                .contentType(APPLICATION_JSON_VALUE)
                .params(condition))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 조회하기 - 게시글 조회 성공")
    void boardSuccessTest() throws Exception{
        Long savedBoardId = createBoard(TEST_EMAIL);
        String testComment = "댓글 내용";
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(testComment)
                .boardId(savedBoardId)
                .build();

        savedBoardId = commentService.createComment(commentCreate.toEntity(), TEST_EMAIL);

        mockMvc.perform(get("/api/board/{id}", savedBoardId)
                .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.title").value(TEST_TITLE))
                .andExpect(jsonPath("data.comments[0].id").value(1L))
                .andExpect(jsonPath("data.comments[0].comment").value(testComment))
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 조회하기 - 없는 게시글일 때")
    void boardFailTest() throws Exception{
        createBoard(TEST_EMAIL);

        mockMvc.perform(get("/api/board/{id}", 0L)
                .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.noBoard", null, getDefault())))
                .andExpect(jsonPath("exceptionName").value(BoardNotFound.class.getSimpleName()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 수정하기 - 글 수정 성공")
    void boardSaveTest() throws Exception{
        Board originalBoard = boardService.getBoard(createBoard(TEST_EMAIL));
        BoardEdit boardEdit = BoardEdit.builder()
                .id(originalBoard.getId())
                .title("수정된 글 제목")
                .content(originalBoard.getContent())
                .tempSave(originalBoard.isTempSave())
                .build();

        mockMvc.perform(patch("/api/member/board")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(boardEdit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("글 수정하기 - 글 번호는 필수전달")
    void requiredBoardIdTest() throws Exception{
        Board originalBoard = boardService.getBoard(createBoard(TEST_EMAIL));
        BoardEdit boardEdit = BoardEdit.builder()
                .title(originalBoard.getTitle())
                .content(originalBoard.getContent())
                .tempSave(originalBoard.isTempSave())
                .build();

        mockMvc.perform(patch("/api/member/board")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(boardEdit)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("fieldErrors[0].field").value("id"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.boardId", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("글 수정하기 - 글 제목은 필수입력")
    void requiredTitleForEditBoardTest() throws Exception{
        Board originalBoard = boardService.getBoard(createBoard(TEST_EMAIL));
        BoardEdit boardEdit = BoardEdit.builder()
                .id(originalBoard.getId())
                .content(originalBoard.getContent())
                .tempSave(originalBoard.isTempSave())
                .build();

        mockMvc.perform(patch("/api/member/board")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(boardEdit)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("fieldErrors[0].field").value("title"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.title", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("글 수정하기 - 글을 작성한 사용자가 아니면 수정 불가")
    void editBoardWriterFailTest() throws Exception{
        Board originalBoard = boardService.getBoard(createBoard(TEST_EMAIL));
        BoardEdit boardEdit = BoardEdit.builder()
                .id(originalBoard.getId())
                .title("수정된 글 제목")
                .content(originalBoard.getContent())
                .tempSave(originalBoard.isTempSave())
                .build();

        mockMvc.perform(patch("/api/member/board")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken(TEST_ANOTHER_EMAIL).getAccessToken())
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(boardEdit)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.cannotEditBoard", null, getDefault())))
                .andDo(print());
    }

    private Long createBoard(String email){
        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .tempSave(false)
                .boardType(DAILY)
                .build();

        return boardService.createBoard(boardCreate.toEntity(), email);
    }
}