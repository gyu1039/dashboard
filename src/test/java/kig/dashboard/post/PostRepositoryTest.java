package kig.dashboard.post;

import kig.dashboard.member.MemberRepository;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.entity.MemberRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 멤버1명_게시글작성() {

        Member admin = Member.builder()
                .role(MemberRole.ADMIN)
                .username("kangig@gmail.com")
                .password("admin")
                .nickname("관리자")
                .build();

        Member savedAdmin = memberRepository.save(admin);
        assertThat(admin).isEqualTo(savedAdmin);

        Post post = Post.builder()
                .writer(admin)
                .title("공지사항")
                .content("첫번째 게시글입니다.")
                .build();

        assertThat(post).isNotNull();

        Post savedPost = postRepository.save(post);

        assertThat(savedPost.getTitle()).isEqualTo("공지사항");
        assertThat(savedPost.getWriter()).isEqualTo(admin);
        assertThat(savedPost.getWriter().getRole()).isEqualTo(MemberRole.ADMIN);
        assertThat(savedPost.getWriter().getUsername()).isEqualTo("kangig@gmail.com");

    }
}