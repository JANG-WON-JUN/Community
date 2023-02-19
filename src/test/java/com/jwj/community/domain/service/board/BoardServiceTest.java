package com.jwj.community.domain.service.board;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.annotation.ServiceTest;
import com.jwj.community.web.common.paging.request.BoardSearchCondition;
import com.jwj.community.web.dto.board.request.BoardCreate;
import com.jwj.community.web.dto.board.request.BoardEdit;
import com.jwj.community.web.dto.board.request.BoardTypeCreate;
import com.jwj.community.web.dto.member.request.BirthDayCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.PasswordCreate;
import com.jwj.community.web.exception.CannotEditBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;

import java.util.stream.IntStream;

import static com.jwj.community.domain.enums.BoardTypes.DAILY;
import static com.jwj.community.domain.enums.Sex.MALE;
import static java.util.Locale.getDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ServiceTest
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    MemberService memberService;

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
    @DisplayName("게시판 저장 성공")
    void saveBoardTest() {
        // given
        BoardTypeCreate boardType = BoardTypeCreate.builder()
                .boardType(DAILY)
                .build();

        BoardCreate boardCreate = BoardCreate.builder()
                .title(TEST_TITLE)
                .content(TEST_CONTENT)
                .tempSave(false)
                .boardType(boardType)
                .build();

        // when
        Long savedId = boardService.createBoard(boardCreate.toEntity(), TEST_EMAIL);
        Member savedMember = memberService.findByEmail(TEST_EMAIL);

        // then
        assertThat(savedId).isEqualTo(1);
        assertThat(savedMember.getBoards().size()).isEqualTo(1);
    }


    @Test
    @DisplayName("글 리스트 조회하기")
    void boardListTest() {
        // given
        IntStream.rangeClosed(1, 7).forEach(i -> createBoard());

        // when
        Page<Board> boardPage = boardService.getBoards(BoardSearchCondition.builder().build());

        // then
        assertThat(boardPage.getContent().isEmpty()).isFalse();
        assertThat(boardPage.getContent().size()).isEqualTo(7);
    }

    @Test
    @DisplayName("글 1개 조회하기 - 게시글 조회 성공")
    void boardSuccessTest() {
        // given
        Long savedBoardId = createBoard();

        // when
        Board savedBoard = boardService.getBoard(savedBoardId);

        // then
        assertThat(savedBoard.getId()).isEqualTo(savedBoardId);
        assertThat(savedBoard.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(savedBoard.getContent()).isEqualTo(TEST_CONTENT);
        assertThat(savedBoard.getViews()).isEqualTo(1);
        assertThat(savedBoard.getBoardType().getBoardType()).isEqualTo(DAILY);
        assertThat(savedBoard.isTempSave()).isFalse();
    }

    @Test
    @DisplayName("글 1개 조회하기 - 없는 게시글일 때")
    void boardSFailTest() {
        // given
        Long invalidBoardId = 0L;

        // expected
        assertThatThrownBy(() -> { boardService.getBoard(invalidBoardId); })
                .isInstanceOf(Exception.class)
                .hasMessage(messageSource.getMessage("error.noBoard", null, getDefault()));
    }

    @Test
    @DisplayName("글 1개 수정하기 - 글 제목 수정하기")
    void editBoardTitleTest() {
        // given
        Board originalBoard = boardService.getBoard(createBoard());
        String originalTitle = originalBoard.getTitle();
        String editedTitle = "수정된 글 제목";
        BoardEdit boardEdit = BoardEdit.builder()
                .id(originalBoard.getId())
                .title(editedTitle)
                .content(originalBoard.getContent())
                .tempSave(originalBoard.isTempSave())
                .build();

        // when
        Long editedBoardId = boardService.editBoard(boardEdit.toEntity(), TEST_EMAIL);
        Board editedBoard = boardService.getBoard(editedBoardId);

        // then
        assertThat(editedBoard.getId()).isEqualTo(originalBoard.getId());
        assertThat(editedBoard.getTitle()).isEqualTo(editedTitle);
        assertThat(editedBoard.getTitle()).isNotEqualTo(originalTitle);
        assertThat(editedBoard.getContent()).isEqualTo(originalBoard.getContent());
        assertThat(editedBoard.isTempSave()).isEqualTo(originalBoard.isTempSave());
    }

    @Test
    @DisplayName("글 1개 수정하기 - 글을 작성한 사용자가 아니면 수정 불가")
    void editBoardWriterFailTest() {
        // given
        Board originalBoard = boardService.getBoard(createBoard());
        BoardEdit boardEdit = BoardEdit.builder()
                .id(originalBoard.getId())
                .title("수정된 글 제목")
                .content(originalBoard.getContent())
                .tempSave(originalBoard.isTempSave())
                .build();

        // expected
        assertThatThrownBy(() -> {
            boardService.editBoard(boardEdit.toEntity(), TEST_ANOTHER_EMAIL);
        })
                .isInstanceOf(CannotEditBoard.class)
                .hasMessage(messageSource.getMessage("error.cannotEditBoard", null, getDefault()));
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