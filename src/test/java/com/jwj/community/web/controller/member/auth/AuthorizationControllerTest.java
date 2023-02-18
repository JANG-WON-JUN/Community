package com.jwj.community.web.controller.member.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.config.security.token.JwtAuthenticationToken;
import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.enums.Roles;
import com.jwj.community.domain.service.board.BoardService;
import com.jwj.community.web.annotation.ControllerTest;
import com.jwj.community.web.dto.board.request.BoardCreate;
import com.jwj.community.web.dto.board.request.BoardTypeCreate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.jwj.community.domain.enums.BoardTypes.DAILY;
import static com.jwj.community.domain.enums.Roles.ROLE_ADMIN;
import static com.jwj.community.domain.enums.Roles.ROLE_MEMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
public class AuthorizationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BoardService boardService;

    private List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    private final String TEST_EMAIL = "admin@google.com";
    private final String TEST_TITLE = "글 제목";
    private final String TEST_CONTENT = "글 내용";

    @Test
    @DisplayName("글 목록 조회시에는 ROLE_ANONYMOUS 권한으로 인가가능")
    void boardListWithAnonymousTest() throws Exception{
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 목록 조회시에는 ROLE_MEMBER 권한으로 인가가능")
    void boardListWithMemberTest() throws Exception{
        setRole(ROLE_MEMBER);

        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 목록 조회시에는 ROLE_ADMIN 권한으로 인가가능")
    void boardListWithAdminTest() throws Exception{
        setRole(ROLE_ADMIN);

        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 조회시에는 ROLE_ANONYMOUS 권한으로 인가가능")
    void boardWithAnonymousTest() throws Exception{
        mockMvc.perform(get("/api/board", 1))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("글 1개 조회시에는 ROLE_MEMBER 권한으로 인가가능")
    void boardWithMemberTest() throws Exception{
        setRole(ROLE_MEMBER);

        mockMvc.perform(get("/api/board", 1))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 조회시에는 ROLE_ADMIN 권한으로 인가가능")
    void boardWithAdminTest() throws Exception{
        setRole(ROLE_ADMIN);

        mockMvc.perform(get("/api/board", 1))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 저장 시에는 ROLE_ANONYMOUS 권한으로 인가 불가능")
    void boardSaveWithAnonymousTest() throws Exception{
        mockMvc.perform(post("/api/member/board"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value(UNAUTHORIZED.value()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 저장 시에는 ROLE_MEMBER 권한으로 인가가능")
    void boardSaveWithMemberTest() throws Exception{
        setRole(ROLE_MEMBER);

        when(boardService.createBoard(any(Board.class), anyString())).thenReturn(1L);

        mockMvc.perform(post("/api/member/board")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(validBoardCreate())))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 저장 시에는 ROLE_ADMIN 권한으로 인가가능")
    void boardSaveWithAdminTest() throws Exception{
        setRole(ROLE_ADMIN);

        when(boardService.createBoard(any(Board.class), anyString())).thenReturn(1L);

        mockMvc.perform(post("/api/member/board")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(validBoardCreate())))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 임시저장 시에는 ROLE_ANONYMOUS 권한으로 인가 불가능")
    void boardTmpSaveWithAnonymousTest() throws Exception{
        mockMvc.perform(post("/api/member/board/tmp"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value(UNAUTHORIZED.value()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 임시저장 시에는 ROLE_MEMBER 권한으로 인가가능")
    void boardTmpSaveWithMemberTest() throws Exception{
        setRole(ROLE_MEMBER);

        mockMvc.perform(post("/api/member/board/tmp"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 임시저장 시에는 ROLE_ADMIN 권한으로 인가가능")
    void boardTmpSaveWithAdminTest() throws Exception{
        setRole(ROLE_ADMIN);

        mockMvc.perform(post("/api/member/board/tmp"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 임시저장 시에는 ROLE_ADMIN 권한으로 인가가능")
    void boardTmpSaveWithAd22minTest() throws Exception{
        setRole(ROLE_ADMIN);

        mockMvc.perform(post("/api/member/board/tmp"))
                .andExpect(status().isOk())
                .andDo(print());

        setRole(ROLE_MEMBER);

        mockMvc.perform(post("/api/member/board/tmp"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    public BoardCreate validBoardCreate(){
        BoardTypeCreate boardType = BoardTypeCreate.builder()
                .boardType(DAILY)
                .build();

        return BoardCreate.builder()
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .tempSave(false)
                .boardType(boardType)
                .build();
    }

    private void setRole(Roles role){
        authorities.clear();
        authorities.add(new SimpleGrantedAuthority(role.name()));

        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext()
                .setAuthentication(new JwtAuthenticationToken(TEST_EMAIL, "1234", authorities));
    }
}
