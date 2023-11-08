package kig.dashboard.post;

import kig.dashboard.comment.Comment;
import kig.dashboard.comment.CommentRepository;
import kig.dashboard.comment.dto.CommentInfoDTO;
import kig.dashboard.member.MemberRole;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.entity.Member;
import kig.dashboard.post.dto.PostInfoDTO;
import kig.dashboard.post.entity.Category;
import kig.dashboard.post.entity.Post;
import kig.dashboard.post.repository.CategoryRepository;
import kig.dashboard.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
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
    CategoryRepository categoryRepository;

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
        assertThat(postInfo.getCommentInfoDTOList().size()).isEqualTo(10);

        List<CommentInfoDTO> commentInfoDTOList = postInfo.getCommentInfoDTOList();
        int total = commentInfoDTOList.size();
        for (CommentInfoDTO commentInfoDTO : commentInfoDTOList) {
            total += commentInfoDTO.getReCommentInfoDTOList().size();
        }
        assertThat(total).isEqualTo(10 * 20 + 10);
    }


    @Test
    public void 카테고리가_추가된_게시글_저장() {

        //given
        Member member = Member.builder()
                .nickname("test")
                .username("test")
                .password("test")
                .build();
        memberRepository.save(member);


        Category category = Category.builder()
                .name("2팀")
                .build();

        Post post1 = Post.builder()
                .title("제목입니다1")
                .content("내용입니다1")
                .category(category)
                .writer(member)
                .build();

        Post post2 = Post.builder()
                .title("제목입니다2")
                .content("내용입니다2")
                .category(category)
                .writer(member)
                .build();

        Post post3 = Post.builder()
                .title("제목입니다3")
                .content("내용입니다3")
                .category(category)
                .writer(member)
                .build();

        // when
        category.addPost(post1);
        category.addPost(post2);
        category.addPost(post3);
        categoryRepository.save(category);


        // then
        List<Post> byCategory = postRepository.findByCategory(category);
        assertThat(byCategory.size()).isEqualTo(3);

    }


    @Test
    public void 카테고리가_추가된_게시글_삭제_카테고리에서_게시글삭제() {

        //given
        Member member = Member.builder()
                .nickname("test")
                .username("test")
                .password("test")
                .build();
        memberRepository.save(member);


        Category category = Category.builder()
                .name("2팀")
                .build();

        Post post1 = Post.builder()
                .title("제목입니다1")
                .content("내용입니다1")
                .category(category)
                .writer(member)
                .build();

        Post post2 = Post.builder()
                .title("제목입니다2")
                .content("내용입니다2")
                .category(category)
                .writer(member)
                .build();

        Post post3 = Post.builder()
                .title("제목입니다3")
                .content("내용입니다3")
                .category(category)
                .writer(member)
                .build();

        category.addPost(post1);
        category.addPost(post2);
        category.addPost(post3);
        categoryRepository.save(category);


        // when
        assertThat(postRepository.findAll().size()).isEqualTo(3);
        category.deletePost(post1);


        // then
        assertThat(postRepository.findByCategory(category).size()).isEqualTo(2);
        assertThat(postRepository.findAll().size()).isEqualTo(3);

    }


}