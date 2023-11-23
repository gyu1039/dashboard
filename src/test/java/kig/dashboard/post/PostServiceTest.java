package kig.dashboard.post;

import kig.dashboard.member.MemberRole;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.MemberService;
import kig.dashboard.member.dto.MemberSignUpDTO;
import kig.dashboard.post.dto.PostSaveDTO;
import kig.dashboard.post.dto.PostUpdateDTO;
import kig.dashboard.post.entity.Post;
import kig.dashboard.post.exception.PostException;
import kig.dashboard.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "PASSWORD";

    private void clear() {
        em.flush();
        em.clear();
    }

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }

    private MockMultipartFile getMockUploadFile() throws IOException {
        return new MockMultipartFile("goo", "goo.png", "image/png",
                new FileInputStream("C:\\Users\\Administrator\\Desktop\\tmp\\goo.png"));
    }

    @BeforeEach
    private void signUpAndSetAuthentication() throws Exception {

        memberService.signUp(new MemberSignUpDTO(USERNAME, PASSWORD, "test"));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        UserDetails build = User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .roles(MemberRole.USER.name())
                .build();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        build, build.getAuthorities())
                );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    @Test
    public void 포스트저장성공_업로드파일없음() throws Exception {

        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content);

        postService.save(postSaveDTO);
        clear();

        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        Post post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFilePath()).isNull();
    }

    @Test
    public void 포스트저장성공_업로드파일있음() throws Exception {

        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content);

        postService.save(postSaveDTO, getMockUploadFile());
        clear();

        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        Post post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFilePath()).isNotNull();

//        deleteFile(post.getFilePath());
    }

    @Test
    public void 게시글저장실패_제목이나내용없음() {

        String title = "제목";
        String content = "내용";

        PostSaveDTO postSaveDTO = new PostSaveDTO(null, content);
        PostSaveDTO postSaveDTO2 = new PostSaveDTO(title, null);

        assertThrows(Exception.class, () -> {
           postService.save(postSaveDTO);
           postService.save(postSaveDTO2);
        });
    }

    @Test
    public void 게시글업데이트성공_업로드파일없음() throws Exception {

        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content);
        postService.save(postSaveDTO);
        clear();

        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        PostUpdateDTO postUpdateDTO = new PostUpdateDTO("바꾼제목", "바꾼 내용");
        postService.update(findPost.getId(), postUpdateDTO, null);
        clear();

        Post post = postRepository.findById(findPost.getId()).get();
        assertThat(post.getContent()).isEqualTo("바꾼 내용");
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFilePath()).isNull();
    }

    @Test
    public void 포스트업데이트성공_파일추가() throws Exception {

        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content);
        postService.save(postSaveDTO);
        clear();

        Post findPost = postRepository.findAll().get(0);
        PostUpdateDTO postUpdateDTO
                = new PostUpdateDTO("바꾼 제목", "바꾼 내용");
        postService.update(findPost.getId(), postUpdateDTO, getMockUploadFile());
        clear();

        Post post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo("바꾼 내용");
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFilePath()).isNotNull();

        deleteFile(post.getFilePath());

    }

    @Test
    public void 게시글업데이트성공_파일제거() throws Exception {

        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content);
        postService.save(postSaveDTO, getMockUploadFile());
        clear();

        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        assertThat(findPost.getFilePath()).isNotNull();
        clear();

        PostUpdateDTO postUpdateDTO = new PostUpdateDTO("updated title", "updated content");
        postService.update(findPost.getId(), postUpdateDTO, null);
        clear();

        Post post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo("updated content");
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFilePath()).isNull();

    }

    @Test
    public void 게시글업데이트성공_파일교체() throws Exception {

        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content);
        postService.save(postSaveDTO, getMockUploadFile());
        clear();

        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        Post post = em.find(Post.class, findPost.getId());
        String filePath = post.getFilePath();
        clear();


        PostUpdateDTO postUpdateDTO
                = new PostUpdateDTO("updated title",
                                    "updated content");

        postService.update(findPost.getId(), postUpdateDTO, new MockMultipartFile("image", "image.png", "image/png",
                new FileInputStream("C:\\Users\\Administrator\\Desktop\\tmp\\image.png")));
        clear();

        Post post1 = em.find(Post.class, findPost.getId());
        assertThat(post1.getContent()).isEqualTo("updated content");
        assertThat(post1.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post1.getFilePath()).isNotEqualTo(filePath);
        deleteFile(post1.getFilePath());
    }

    private void setAnotherAuthentication() throws Exception {

        memberService.signUp(new MemberSignUpDTO(USERNAME+"123",PASSWORD, "nickName"));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME+"123")
                                .password(PASSWORD)
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    @Test
    public void 게시글업데이트실패_권한이없음() throws Exception {

        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content);
        postService.save(postSaveDTO, getMockUploadFile());
        clear();

        setAnotherAuthentication();

        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        PostUpdateDTO postUpdateDTO = new PostUpdateDTO("제목1", "내용1");

        assertThrows(PostException.class, () -> postService.update(findPost.getId(), postUpdateDTO, null));
    }

    @Test
    public void 게시글삭제_성공() throws Exception {
        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content);
        postService.save(postSaveDTO);
        clear();

        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        postService.delete(findPost.getId());

        List<Post> all = postRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
    }

    @Test
    public void 게시글삭제_실패() throws Exception {

        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content);
        postService.save(postSaveDTO);
        clear();

        setAnotherAuthentication();

        Post findPost = em.createQuery("select p from Post p ", Post.class).getSingleResult();
        assertThrows(PostException.class, () -> postService.delete(findPost.getId()));
    }

    @Test
    public void 게시글_검색_조건없음() {


    }
}