package com.jwj.community.domain.service.board;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.annotation.ServiceTest;
import com.jwj.community.web.dto.board.request.BoardCreate;
import com.jwj.community.web.dto.board.request.BoardTypeCreate;
import com.jwj.community.web.dto.member.request.BirthDayCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.PasswordCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static com.jwj.community.domain.enums.BoardTypes.DAILY;
import static com.jwj.community.domain.enums.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;

@ServiceTest
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    MemberService memberService;

    private final String TEST_EMAIL = "admin@google.com";
    private final String TEST_PASSWORD = "1234";
    private final String TEST_TITLE = "글 제목";
    private final String TEST_CONTENT = "글 내용";

    private Member savedMember;

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

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPassword().toEntity());
        savedMember = memberService.findByEmail(TEST_EMAIL);
        System.out.println();
    }

    @Test
    @DisplayName("게시판 저장 성공")
    @Rollback(false)
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

        // then
        assertThat(savedId).isEqualTo(1);
        assertThat(savedMember.getBoards().size()).isEqualTo(1);
    }

}