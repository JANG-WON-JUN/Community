package com.jwj.community.domain.service.comment;

import com.jwj.community.domain.entity.board.Comment;
import com.jwj.community.domain.service.board.BoardService;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.annotation.ServiceTest;
import com.jwj.community.web.dto.board.request.BoardCreate;
import com.jwj.community.web.dto.board.request.BoardRetrieve;
import com.jwj.community.web.dto.board.request.BoardTypeCreate;
import com.jwj.community.web.dto.comment.request.CommentCreate;
import com.jwj.community.web.dto.comment.request.CommentEdit;
import com.jwj.community.web.dto.comment.request.parentComment;
import com.jwj.community.web.dto.member.request.BirthDayCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.PasswordCreate;
import com.jwj.community.web.exception.BoardNotFound;
import com.jwj.community.web.exception.CannotEditBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.List;

import static com.jwj.community.domain.enums.BoardTypes.DAILY;
import static com.jwj.community.domain.enums.Sex.MALE;
import static java.lang.Long.MAX_VALUE;
import static java.util.Locale.getDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ServiceTest
class CommentServiceTest {

    @Autowired
    CommentService commentService;

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
    private final String TEST_COMMENT = "댓글 내용";

    private Long savedBoardId;

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

        // 게시글 등록
        BoardTypeCreate boardType = BoardTypeCreate.builder()
                .boardType(DAILY)
                .build();

        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .tempSave(false)
                .boardType(boardType)
                .build();

        savedBoardId = boardService.createBoard(boardCreate.toEntity(), TEST_EMAIL);
    }

    @Test
    @DisplayName("나의 게시글에 내가 댓글 작성 성공")
    void commentCreateTest() {
        // given
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .board(validBoardRetrieve())
                .build();

        // when
        Long savedBoardId = commentService.createComment(commentCreate.toEntity(), TEST_EMAIL);
        List<Comment> comments = commentService.getComments(savedBoardId);

        // then
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.isEmpty()).isFalse();
        assertThat(comments.get(0).getId()).isEqualTo(1L);
        assertThat(comments.get(0).getCommentGroup()).isEqualTo(1);
        assertThat(comments.get(0).getCommentOrder()).isEqualTo(1);
        assertThat(comments.get(0).getParent()).isNull();
        assertThat(comments.get(0).getComment()).isEqualTo(TEST_COMMENT);
        assertThat(comments.get(0).getMember().getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("나의 게시글에 다른 사람이 댓글 작성 성공")
    void commentCreateMyBoardTest() {
        // given
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .board(validBoardRetrieve())
                .build();

        // when
        Long savedBoardId = commentService.createComment(commentCreate.toEntity(), TEST_ANOTHER_EMAIL);
        List<Comment> comments = commentService.getComments(savedBoardId);

        // then
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.isEmpty()).isFalse();
        assertThat(comments.get(0).getId()).isEqualTo(1L);
        assertThat(comments.get(0).getCommentGroup()).isEqualTo(1);
        assertThat(comments.get(0).getCommentOrder()).isEqualTo(1);
        assertThat(comments.get(0).getParent()).isNull();
        assertThat(comments.get(0).getComment()).isEqualTo(TEST_COMMENT);
        assertThat(comments.get(0).getMember().getEmail()).isEqualTo(TEST_ANOTHER_EMAIL);
    }

    @Test
    @DisplayName("대댓글 달기 - 댓글 -> 대댓글 순서로 조회")
    void nestCommentCreateTest1() {
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .board(validBoardRetrieve())
                .build();

        CommentCreate nestedCommentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .parent(validParentComment())
                .board(validBoardRetrieve())
                .build();

        commentService.createComment(commentCreate.toEntity(), TEST_EMAIL);
        Long savedBoardId = commentService.createComment(nestedCommentCreate.toEntity(), TEST_ANOTHER_EMAIL);

        // when
        List<Comment> comments = commentService.getComments(savedBoardId);

        // then
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.isEmpty()).isFalse();

        assertThat(comments.get(0).getId()).isEqualTo(1L);
        assertThat(comments.get(0).getCommentGroup()).isEqualTo(1);
        assertThat(comments.get(0).getCommentOrder()).isEqualTo(1);
        assertThat(comments.get(0).getParent()).isNull();
        assertThat(comments.get(0).getComment()).isEqualTo(TEST_COMMENT);
        assertThat(comments.get(0).getMember().getEmail()).isEqualTo(TEST_EMAIL);

        assertThat(comments.get(1).getId()).isEqualTo(2L);
        assertThat(comments.get(1).getCommentGroup()).isEqualTo(1);
        assertThat(comments.get(1).getCommentOrder()).isEqualTo(2);
        assertThat(comments.get(1).getParent()).isNotNull();
        assertThat(comments.get(1).getComment()).isEqualTo(TEST_COMMENT);
        assertThat(comments.get(1).getMember().getEmail()).isEqualTo(TEST_ANOTHER_EMAIL);
    }

    @Test
    @DisplayName("대댓글 여러 개 달기 - 댓글 -> 대댓글 순서로 조회")
    void nestCommentCreateTest2() {
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .board(validBoardRetrieve())
                .build();

        CommentCreate nestedCommentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .parent(validParentComment())
                .board(validBoardRetrieve())
                .build();

        CommentCreate nestedCommentCreate2 = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .parent(validParentComment())
                .board(validBoardRetrieve())
                .build();

        commentService.createComment(commentCreate.toEntity(), TEST_EMAIL);
        commentService.createComment(nestedCommentCreate.toEntity(), TEST_ANOTHER_EMAIL);
        Long savedBoardId = commentService.createComment(nestedCommentCreate2.toEntity(), TEST_ANOTHER_EMAIL);

        // when
        List<Comment> comments = commentService.getComments(savedBoardId);

        // then
        assertThat(comments.size()).isEqualTo(3);
        assertThat(comments.isEmpty()).isFalse();

        assertThat(comments.get(0).getId()).isEqualTo(1L);
        assertThat(comments.get(0).getCommentGroup()).isEqualTo(1);
        assertThat(comments.get(0).getCommentOrder()).isEqualTo(1);
        assertThat(comments.get(0).getParent()).isNull();
        assertThat(comments.get(0).getComment()).isEqualTo(TEST_COMMENT);
        assertThat(comments.get(0).getMember().getEmail()).isEqualTo(TEST_EMAIL);

        assertThat(comments.get(1).getId()).isEqualTo(2L);
        assertThat(comments.get(1).getCommentGroup()).isEqualTo(1);
        assertThat(comments.get(1).getCommentOrder()).isEqualTo(2);
        assertThat(comments.get(1).getParent()).isNotNull();
        assertThat(comments.get(1).getComment()).isEqualTo(TEST_COMMENT);
        assertThat(comments.get(1).getMember().getEmail()).isEqualTo(TEST_ANOTHER_EMAIL);

        assertThat(comments.get(2).getId()).isEqualTo(3L);
        assertThat(comments.get(2).getCommentGroup()).isEqualTo(1);
        assertThat(comments.get(2).getCommentOrder()).isEqualTo(3);
        assertThat(comments.get(2).getParent()).isNotNull();
        assertThat(comments.get(2).getComment()).isEqualTo(TEST_COMMENT);
        assertThat(comments.get(2).getMember().getEmail()).isEqualTo(TEST_ANOTHER_EMAIL);
    }

    @Test
    @DisplayName("대댓글 달고 그냥 댓글 달기 - 댓글 -> 대댓글 -> 댓글 순서로 조회")
    void nestCommentCreateTest3() {
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .board(validBoardRetrieve())
                .build();

        CommentCreate commentCreate2 = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .board(validBoardRetrieve())
                .build();

        CommentCreate nestedCommentCreate2 = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .parent(validParentComment())
                .board(validBoardRetrieve())
                .build();

        commentService.createComment(commentCreate.toEntity(), TEST_EMAIL);
        commentService.createComment(commentCreate2.toEntity(), TEST_ANOTHER_EMAIL);
        Long savedBoardId = commentService.createComment(nestedCommentCreate2.toEntity(), TEST_ANOTHER_EMAIL);

        // when
        List<Comment> comments = commentService.getComments(savedBoardId);

        // then
        assertThat(comments.size()).isEqualTo(3);
        assertThat(comments.isEmpty()).isFalse();

        assertThat(comments.get(0).getId()).isEqualTo(1L);
        assertThat(comments.get(0).getCommentGroup()).isEqualTo(1);
        assertThat(comments.get(0).getCommentOrder()).isEqualTo(1);
        assertThat(comments.get(0).getParent()).isNull();
        assertThat(comments.get(0).getComment()).isEqualTo(TEST_COMMENT);
        assertThat(comments.get(0).getMember().getEmail()).isEqualTo(TEST_EMAIL);

        assertThat(comments.get(1).getId()).isEqualTo(3L);
        assertThat(comments.get(1).getCommentGroup()).isEqualTo(1);
        assertThat(comments.get(1).getCommentOrder()).isEqualTo(2);
        assertThat(comments.get(1).getParent()).isNotNull();
        assertThat(comments.get(1).getComment()).isEqualTo(TEST_COMMENT);
        assertThat(comments.get(1).getMember().getEmail()).isEqualTo(TEST_ANOTHER_EMAIL);

        assertThat(comments.get(2).getId()).isEqualTo(2L);
        assertThat(comments.get(2).getCommentGroup()).isEqualTo(2);
        assertThat(comments.get(2).getCommentOrder()).isEqualTo(1);
        assertThat(comments.get(2).getParent()).isNull();
        assertThat(comments.get(2).getComment()).isEqualTo(TEST_COMMENT);
        assertThat(comments.get(2).getMember().getEmail()).isEqualTo(TEST_ANOTHER_EMAIL);
    }

    @Test
    @DisplayName("게시글이 존재하지 않으면 예외발생")
    void boardNotFoundTest() {
        // given
        BoardRetrieve boardRetrieve = BoardRetrieve.builder()
                .boardId(MAX_VALUE)
                .build();

        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .board(boardRetrieve)
                .build();

        // expected
        assertThatThrownBy(() -> commentService.createComment(commentCreate.toEntity(), TEST_EMAIL))
                .isInstanceOf(BoardNotFound.class)
                .hasMessage(messageSource.getMessage("error.noBoard", null, getDefault()));
    }

    @Test
    @DisplayName("댓글 수정하기")
    void commentEditTest() {
        // given
        String editedComment = "수정된 댓글 내용";
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .board(validBoardRetrieve())
                .build();

        CommentEdit commentEdit = CommentEdit.builder()
                .id(1L)
                .comment(editedComment)
                .build();

        Long savedBoardId = commentService.createComment(commentCreate.toEntity(), TEST_EMAIL);

        // when
        commentService.editComment(commentEdit.toEntity(), TEST_EMAIL);

        List<Comment> comments = commentService.getComments(savedBoardId);

        // then
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.isEmpty()).isFalse();
        assertThat(comments.get(0).getId()).isEqualTo(1L);
        assertThat(comments.get(0).getCommentGroup()).isEqualTo(1);
        assertThat(comments.get(0).getCommentOrder()).isEqualTo(1);
        assertThat(comments.get(0).getParent()).isNull();
        assertThat(comments.get(0).getComment()).isEqualTo(editedComment);
        assertThat(comments.get(0).getMember().getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("댓글 수정하기 - 내가 쓴 댓글이 아니면 수정 불가")
    void commentEditFailTest() {
        // given
        String editedComment = "수정된 댓글 내용";
        CommentCreate commentCreate = CommentCreate.builder()
                .comment(TEST_COMMENT)
                .board(validBoardRetrieve())
                .build();

        CommentEdit commentEdit = CommentEdit.builder()
                .id(1L)
                .comment(editedComment)
                .build();

        commentService.createComment(commentCreate.toEntity(), TEST_EMAIL);

        // expected
        assertThatThrownBy(() -> commentService.editComment(commentEdit.toEntity(), TEST_ANOTHER_EMAIL))
                .isInstanceOf(CannotEditBoard.class)
                .hasMessage(messageSource.getMessage("error.cannotEditComment", null, getDefault()));
    }

    private BoardRetrieve validBoardRetrieve(){
        return BoardRetrieve.builder()
                .boardId(savedBoardId)
                .build();
    }

    private parentComment validParentComment(){
        return parentComment.builder()
                .parentId(1L)
                .build();
    }
}