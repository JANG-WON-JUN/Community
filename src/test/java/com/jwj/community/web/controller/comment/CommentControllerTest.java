package com.jwj.community.web.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.config.security.utils.code.JwtTokenFactory;
import com.jwj.community.domain.service.board.BoardService;
import com.jwj.community.domain.service.comment.CommentService;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.annotation.ControllerTest;
import com.jwj.community.web.dto.board.request.BoardCreate;
import com.jwj.community.web.dto.comment.request.CommentCreate;
import com.jwj.community.web.dto.comment.request.CommentEdit;
import com.jwj.community.web.dto.member.request.MemberCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;

import static com.jwj.community.domain.enums.BoardTypes.DAILY;
import static com.jwj.community.domain.enums.Sex.MALE;
import static com.jwj.community.web.common.consts.JwtConst.AUTHORIZATION;
import static java.util.Locale.getDefault;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class CommentControllerTest {

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
    private final String TEST_COMMENT = "?????? ??????";
    private final JwtTokenFactory jwtTokenFactory = new JwtTokenFactory();

    private Long savedBoardId;

    @BeforeEach
    public void setup(){
        // ?????? ??????
        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .name("?????????")
                .nickname("????????? ?????????")
                .password("1234")
                .year(2023)
                .month(1)
                .day(28)
                .sex(MALE)
                .build();

        MemberCreate anotherMemberCreate = MemberCreate.builder()
                .email(TEST_ANOTHER_EMAIL)
                .name("??????")
                .nickname("?????? ?????????")
                .password("1234")
                .year(2023)
                .month(1)
                .day(28)
                .sex(MALE)
                .build();

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPasswordEntity());
        memberService.createMember(anotherMemberCreate.toEntity(), anotherMemberCreate.getPasswordEntity());

        // ????????? ??????
        BoardCreate boardCreate = BoardCreate.builder()
                .title("??? ??????")
                .content("??? ??????")
                .tempSave(false)
                .boardType(DAILY)
                .build();

        savedBoardId = boardService.createBoard(boardCreate.toEntity(), TEST_EMAIL);
    }

    @Test
    @DisplayName("?????? ???????????? - ???????????? ?????? ??? ??????")
    void createWithAllInfoTest() throws Exception{
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .boardId(savedBoardId)
                .build();

        mockMvc.perform(post("/api/member/comment")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(commentCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(savedBoardId))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ???????????? - ???????????? ?????? ??? ??????")
    void createNestedCommentWithAllInfoTest() throws Exception{
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .boardId(savedBoardId)
                .build();

        CommentCreate nestedCommentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .parentId(1L)
                .boardId(savedBoardId)
                .build();

        commentService.createComment(commentCreate.toEntity(), TEST_EMAIL);

        mockMvc.perform(post("/api/member/comment")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(nestedCommentCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(savedBoardId))
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ???????????? - ?????? ????????? ????????????")
    void requiredCommentTest() throws Exception{
        CommentCreate commentCreate = CommentCreate.builder()
                .boardId(savedBoardId)
                .build();

        mockMvc.perform(post("/api/member/comment")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(commentCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("comment"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.comment", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ???????????? - ????????? ????????? ????????????")
    void requiredBoardIdTest() throws Exception{
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .build();

        mockMvc.perform(post("/api/member/comment")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(commentCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("boardId"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.board", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ???????????? - ???????????? ?????? ??? ??????")
    void editCommentWithAllInfoTest() throws Exception{
        String editedComment = "????????? ?????? ??????";
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .boardId(savedBoardId)
                .build();

        CommentEdit commentEdit = CommentEdit.builder()
                .id(1L)
                .comment(editedComment)
                .build();

        commentService.createComment(commentCreate.toEntity(), TEST_EMAIL);

        mockMvc.perform(patch("/api/member/comment")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(commentEdit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").value(savedBoardId))
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ???????????? - ??????????????? ????????????")
    void requiredCommentIdTest() throws Exception{
        String editedComment = "????????? ?????? ??????";
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .boardId(savedBoardId)
                .build();

        CommentEdit commentEdit = CommentEdit.builder()
                .comment(editedComment)
                .build();

        commentService.createComment(commentCreate.toEntity(), TEST_EMAIL);

        mockMvc.perform(patch("/api/member/comment")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(commentEdit)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("id"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.commentId", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ???????????? - ????????? ????????? ???????????? ????????? ?????? ??????")
    void editCommentWriterFailTest() throws Exception{
        String editedComment = "????????? ?????? ??????";
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .boardId(savedBoardId)
                .build();

        CommentEdit commentEdit = CommentEdit.builder()
                .id(1L)
                .comment(editedComment)
                .build();

        commentService.createComment(commentCreate.toEntity(), TEST_EMAIL);

        mockMvc.perform(patch("/api/member/comment")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken(TEST_ANOTHER_EMAIL).getAccessToken())
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(commentEdit)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.cannotEditComment", null, getDefault())))
                .andDo(print());
    }
}