package kig.dashboard.post;

import kig.dashboard.comment.Comment;
import kig.dashboard.comment.CommentRepository;
import kig.dashboard.comment.dto.CommentInfoDTO;
import kig.dashboard.member.MemberRepository;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.entity.MemberRole;
import kig.dashboard.post.dto.PostInfoDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostService postService;

    @Autowired
    EntityManager em;

    @Test
    public void 게시글조회() {

        Member member1 = memberRepository.save(Member.builder()
                .username("username1")
                .password("test")
                .nickname("test1")
                .role(MemberRole.USER)
                .build());
        Member member2 = memberRepository.save(Member.builder()
                .username("username2")
                .password("test")
                .nickname("test2")
                .role(MemberRole.USER)
                .build());
        Member member3 = memberRepository.save(Member.builder()
                .username("username3")
                .password("test")
                .nickname("test3")
                .role(MemberRole.USER)
                .build());
        Member member4 = memberRepository.save(Member.builder()
                .username("username4")
                .password("test")
                .nickname("test4")
                .role(MemberRole.USER)
                .build());
        Member member5 = memberRepository.save(Member.builder()
                .username("username5")
                .password("test")
                .nickname("test5")
                .role(MemberRole.USER)
                .build());

        Map<Integer, Long> memberMap = new HashMap<>();
        memberMap.put(1, member1.getId());
        memberMap.put(2, member2.getId());
        memberMap.put(3, member3.getId());
        memberMap.put(4, member4.getId());
        memberMap.put(5, member5.getId());

        Post post = Post.builder().title("게시글").content("내용").build();
        post.confirmWriter(member1);
        postRepository.save(post);
        em.flush();


        for(int i=1; i<=10; i++) {
            Comment comment = Comment.builder().content("댓글" + i).build();
            comment.confirmWriter(Objects.requireNonNull(memberRepository.findById(memberMap.get(i % 5 + 1)).orElse(null)));
            comment.confirmPost(post);
            commentRepository.save(comment);
        }

        commentRepository.findAll().forEach(comment -> {

            IntStream.range(1, 21).forEach(i -> {
                Comment recomment = Comment.builder().content("자식댓글" + i).build();
                recomment.confirmWriter(Objects.requireNonNull(memberRepository.findById(memberMap.get(i % 5 + 1)).orElse(null)));
                recomment.confirmPost(comment.getPost());
                recomment.confirmParent(comment);
            });
        });

        em.flush();

        PostInfoDTO postInfo = postService.getPostInfo(post.getId());

        assertThat(postInfo.getWriterDTO()).isNotNull();

        assertThat(postInfo.getPostId()).isEqualTo(post.getId());
        assertThat(postInfo.getContent()).isEqualTo(post.getContent());
        assertThat(postInfo.getWriterDTO().getUsername()).isEqualTo(post.getWriter().getUsername());

        int reCommentCount = 0;
        for (CommentInfoDTO commentInfoDTO : postInfo.getCommentInfoDTOList()) {
            reCommentCount += commentInfoDTO.getReCommentInfoDTOList().size();
        }

        assertThat(postInfo.getCommentInfoDTOList().size()).isEqualTo(10);
        assertThat(reCommentCount).isEqualTo(10 * 20);

    }

}