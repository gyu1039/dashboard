package kig.dashboard.post;

import kig.dashboard.member.MemberRole;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.entity.Member;
import kig.dashboard.post.cond.PostSearchCondition;
import kig.dashboard.post.dto.PostPagingDTO;
import kig.dashboard.post.entity.Post;
import kig.dashboard.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomPostRepositoryImplTest {


    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Test
    public void 포스트_검색_조건없음() {

        Member member = memberRepository.save(Member.builder().username("test").password("test").nickname("test").role(MemberRole.USER).build());

        final int POST_COUNT = 50;

        for(int i=1; i<=POST_COUNT; i++) {
            Post post = Post.builder().title("게시글" + i).content("내용" + i).build();
            post.confirmWriter(member);
            postRepository.save(post);
        }

        final int PAGE = 0;
        final int SIZE = 20;
        PageRequest pageRequest = PageRequest.of(PAGE, SIZE);

        PostSearchCondition postSearchCondition = new PostSearchCondition();

        PostPagingDTO postList = postService.searchWithConditions(postSearchCondition, pageRequest);

        assertThat(postList.getTotalElementCount()).isEqualTo(POST_COUNT);
        assertThat(postList.getTotalPageCount()).isEqualTo(POST_COUNT/SIZE + 1);
        assertThat(postList.getCurrentPageNum()).isEqualTo(PAGE);
        assertThat(postList.getCurrentPageElementCount()).isEqualTo(SIZE);
    }

    @Test
    public void 포스트_검색_제목일치() {

        Member member = memberRepository.save(Member.builder().username("test").password("1234").nickname("test").role(MemberRole.USER).build());

        final int POST_COUNT = 50;
        for(int i=1; i<=POST_COUNT; i++) {
            Post post = Post.builder().title("게시글" + i).content("내용" + i).build();
            post.confirmWriter(member);
            postRepository.save(post);
        }

        final String SEARCH_TITLE_STRING = "AAA";
        final int COND_POST_COUNT = 100;
        for(int i=1; i<=COND_POST_COUNT; i++) {
            Post post = Post.builder().title(SEARCH_TITLE_STRING + i).content("내용" + i).build();
            post.confirmWriter(member);
            postRepository.save(post);
        }

        final int PAGE = 2;
        final int SIZE = 20;
        PageRequest pageRequest = PageRequest.of(PAGE, SIZE);

        PostSearchCondition postSearchCondition = new PostSearchCondition();
        postSearchCondition.setTitle(SEARCH_TITLE_STRING);

        PostPagingDTO postList = postService.searchWithConditions(postSearchCondition, pageRequest);

        assertThat(postList.getTotalElementCount()).isEqualTo(COND_POST_COUNT);
        assertThat(postList.getTotalPageCount()).isEqualTo(COND_POST_COUNT  / SIZE);
        assertThat(postList.getCurrentPageNum()).isEqualTo(PAGE);
        assertThat(postList.getCurrentPageElementCount()).isEqualTo(SIZE);
    }

    @Test
    public void 포스트_검색_내용일치() {

        Member member = memberRepository.save(Member.builder().username("test").password("1234").nickname("test").role(MemberRole.USER).build());

        final int POST_COUNT = 100;
        for(int i=1; i<=POST_COUNT; i++) {
            Post post = Post.builder().title("게시글" + i).content("내용" + i).build();
            post.confirmWriter(member);
            postRepository.save(post);
        }

        final String SEARCH_CONTENT_STRING = "AAA";
        final int COND_POST_COUNT = 100;
        for(int i=1; i<=COND_POST_COUNT; i++) {
            Post post = Post.builder().title("게시글" + i).content(SEARCH_CONTENT_STRING + i).build();
            post.confirmWriter(member);
            postRepository.save(post);
        }

        final int PAGE = 2;
        final int SIZE = 20;
        PageRequest pageRequest = PageRequest.of(PAGE, SIZE);

        PostSearchCondition postSearchCondition = new PostSearchCondition();
        postSearchCondition.setContent(SEARCH_CONTENT_STRING);

        PostPagingDTO postList = postService.searchWithConditions(postSearchCondition, pageRequest);

        assertThat(postList.getTotalElementCount()).isEqualTo(COND_POST_COUNT);
        assertThat(postList.getTotalPageCount()).isEqualTo(COND_POST_COUNT  / SIZE);
        assertThat(postList.getCurrentPageNum()).isEqualTo(PAGE);
        assertThat(postList.getCurrentPageElementCount()).isEqualTo(SIZE);
    }

    @Test
    public void 포스트_검색_제목과내용일치() {

        Member member = memberRepository.save(Member.builder().username("test").password("1234").nickname("test").role(MemberRole.USER).build());

        final int POST_COUNT = 100;
        for(int i=1; i<=POST_COUNT; i++) {
            Post post = Post.builder().title("게시글" + i).content("내용" + i).build();
            post.confirmWriter(member);
            postRepository.save(post);
        }

        final String SEARCH_TITLE_STRING = "AAA";
        final String SEARCH_CONTENT_STRING = "BBB";
        final int COND_POST_COUNT = 100;
        for(int i=1; i<=COND_POST_COUNT; i++) {
            Post post = Post.builder().title(SEARCH_TITLE_STRING + i).content(SEARCH_CONTENT_STRING + i).build();
            post.confirmWriter(member);
            postRepository.save(post);
        }

        for(int i=1; i<=COND_POST_COUNT; i++) {
            Post post = Post.builder().title("게시글" + i).content(SEARCH_CONTENT_STRING + i).build();
            post.confirmWriter(member);
            postRepository.save(post);
        }


        final int PAGE = 2;
        final int SIZE = 20;
        PageRequest pageRequest = PageRequest.of(PAGE, SIZE);

        PostSearchCondition postSearchCondition = new PostSearchCondition();
        postSearchCondition.setContent(SEARCH_CONTENT_STRING);
        postSearchCondition.setTitle(SEARCH_TITLE_STRING);

        PostPagingDTO postList = postService.searchWithConditions(postSearchCondition, pageRequest);

        assertThat(postList.getTotalElementCount()).isEqualTo(COND_POST_COUNT);
        assertThat(postList.getTotalPageCount()).isEqualTo(COND_POST_COUNT  / SIZE);
        assertThat(postList.getCurrentPageNum()).isEqualTo(PAGE);
        assertThat(postList.getCurrentPageElementCount()).isEqualTo(SIZE);

    }

}