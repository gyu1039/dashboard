package kig.dashboard.board;

import kig.dashboard.member.MemberRepository;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.entity.MemberRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 멤버1명_게시글작성() {

        Member admin = Member.builder()
                .role(MemberRole.ADMIN)
                .email("kangig@gmail.com")
                .password("admin")
                .nickname("관리자")
                .build();

        Member savedAdmin = memberRepository.save(admin);
        assertThat(admin).isEqualTo(savedAdmin);

        Board board = Board.builder()
                .writer(admin)
                .title("공지사항")
                .content("첫번째 게시글입니다.")
                .createdAt(LocalDateTime.now())
                .build();

        assertThat(board).isNotNull();

        Board savedBoard = boardRepository.save(board);

        assertThat(savedBoard.getTitle()).isEqualTo("공지사항");
        assertThat(savedBoard.getWriter()).isEqualTo(admin);
        assertThat(savedBoard.getWriter().getRole()).isEqualTo(MemberRole.ADMIN);
        assertThat(savedBoard.getWriter().getEmail()).isEqualTo("kangig@gmail.com");

    }
}