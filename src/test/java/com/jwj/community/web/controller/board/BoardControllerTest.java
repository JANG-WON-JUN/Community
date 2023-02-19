package com.jwj.community.web.controller.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.config.security.token.JwtAuthenticationToken;
import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.service.board.BoardService;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.annotation.ControllerTest;
import com.jwj.community.web.common.paging.request.BoardSearchCondition;
import com.jwj.community.web.dto.board.request.BoardCreate;
import com.jwj.community.web.dto.board.request.BoardEdit;
import com.jwj.community.web.dto.board.request.BoardTypeCreate;
import com.jwj.community.web.dto.member.request.BirthDayCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.PasswordCreate;
import com.jwj.community.web.exception.BoardNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.jwj.community.domain.enums.BoardTypes.DAILY;
import static com.jwj.community.domain.enums.Roles.ROLE_MEMBER;
import static com.jwj.community.domain.enums.Sex.MALE;
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
    private BoardService boardService;

    @Autowired
    MessageSource messageSource;

    private final String TEST_EMAIL = "admin@google.com";
    private final String TEST_ANOTHER_EMAIL = "member@google.com";
    private final String TEST_PASSWORD = "1234";
    private final String TEST_TITLE = "글 제목";
    private final String TEST_CONTENT = "글 내용";

    @BeforeEach
    public void setup(){
        // 회원 등록
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(2023)
                .month(1)
                .day(28)
                .build();

        PasswordCreate passwordCreate = PasswordCreate.builder()
                .password(TEST_PASSWORD)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .name("어드민")
                .nickname("어드민 닉네임")
                .password(passwordCreate)
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        MemberCreate anotherMemberCreate = MemberCreate.builder()
                .email(TEST_ANOTHER_EMAIL)
                .name("회원")
                .nickname("회원 닉네임")
                .password(passwordCreate)
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPassword().toEntity());
        memberService.createMember(anotherMemberCreate.toEntity(), anotherMemberCreate.getPassword().toEntity());
    }

    @Test
    @WithMockUser
    @DisplayName("글 등록하기 - 모든정보 입력 시 성공")
    void createWithAllInfoTest() throws Exception{
        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .tempSave(false)
                .boardType(validBoardTypeCreate())
                .build();

        mockMvc.perform(post("/api/member/board")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(boardCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(1L))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("글 등록하기 - 글 제목은 필수입력")
    void titleRequiredTest() throws Exception{
        BoardCreate boardCreate = BoardCreate.builder()
                .content(TEST_CONTENT)
                .tempSave(false)
                .boardType(validBoardTypeCreate())
                .build();

        mockMvc.perform(post("/api/member/board")
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
    @WithMockUser
    @DisplayName("글 등록하기 - 글 내용은 빈값허용")
    void allowBlankContentTest() throws Exception{
        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content("")
                .tempSave(false)
                .boardType(validBoardTypeCreate())
                .build();

        mockMvc.perform(post("/api/member/board")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(boardCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(1L))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("글 등록하기 - 글 내용은 null 허용")
    void allowNullContentTest() throws Exception{
        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content(null)
                .tempSave(false)
                .boardType(validBoardTypeCreate())
                .build();

        mockMvc.perform(post("/api/member/board")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(boardCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(1L))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("글 등록하기 - 게시판 타입은 필수입력")
    void boardTypeRequiredTest() throws Exception{
        BoardTypeCreate boardTypeCreate = BoardTypeCreate.builder()
                .boardType(null)
                .build();

        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content(null)
                .tempSave(false)
                .boardType(boardTypeCreate)
                .build();

        mockMvc.perform(post("/api/member/board")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(boardCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("boardType.boardType"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.boardType", null, getDefault())))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("글 리스트 조회하기")
    void boardListTest() throws Exception{
        IntStream.rangeClosed(1, 7).forEach(i -> createBoard());

        mockMvc.perform(get("/api/board")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(BoardSearchCondition.builder().build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size").value(7))
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 조회하기 - 게시글 조회 성공")
    void boardSuccessTest() throws Exception{
        Long savedBoardId = createBoard();

        mockMvc.perform(get("/api/board/{id}", savedBoardId)
                .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 조회하기 - 없는 게시글일 때")
    void boardFailTest() throws Exception{
        createBoard();

        mockMvc.perform(get("/api/board/{id}", 0L)
                .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.noBoard", null, getDefault())))
                .andExpect(jsonPath("exceptionName").value(BoardNotFound.class.getSimpleName()))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("글 수정하기 - 글 수정 성공")
    void boardSaveTest() throws Exception{
        Board originalBoard = boardService.getBoard(createBoard());
        BoardEdit boardEdit = BoardEdit.builder()
                .id(originalBoard.getId())
                .title("수정된 글 제목")
                .content(originalBoard.getContent())
                .tempSave(originalBoard.isTempSave())
                .build();

        mockMvc.perform(patch("/api/member/board/{id}", 1L)
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(boardEdit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(1L))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("글 수정하기 - 글 제목은 필수입력")
    void editBoardRequiredTitleTest() throws Exception{
        Board originalBoard = boardService.getBoard(createBoard());
        BoardEdit boardEdit = BoardEdit.builder()
                .id(originalBoard.getId())
                .content(originalBoard.getContent())
                .tempSave(originalBoard.isTempSave())
                .build();

        mockMvc.perform(patch("/api/member/board/{id}", 1L)
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
        Board originalBoard = boardService.getBoard(createBoard());
        BoardEdit boardEdit = BoardEdit.builder()
                .id(originalBoard.getId())
                .title("수정된 글 제목")
                .content(originalBoard.getContent())
                .tempSave(originalBoard.isTempSave())
                .build();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.clear();
        authorities.add(new SimpleGrantedAuthority(ROLE_MEMBER.name()));

        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext()
                .setAuthentication(new JwtAuthenticationToken(TEST_ANOTHER_EMAIL, "1234", authorities));

        mockMvc.perform(patch("/api/member/board/{id}", 1L)
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(boardEdit)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.cannotEditBoard", null, getDefault())))
                .andDo(print());
    }

    private BoardTypeCreate validBoardTypeCreate(){
        return BoardTypeCreate.builder()
                .boardType(DAILY)
                .build();
    }

    private Long createBoard(){
        BoardTypeCreate boardType = BoardTypeCreate.builder()
                .boardType(DAILY)
                .build();

        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .tempSave(false)
                .boardType(boardType)
                .build();

        return boardService.createBoard(boardCreate.toEntity(), TEST_EMAIL);
    }
}