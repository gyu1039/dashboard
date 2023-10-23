package kig.dashboard.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import kig.dashboard.comment.dto.CommentSaveDTO;
import kig.dashboard.global.config.jwt.JwtService;
import kig.dashboard.member.MemberRepository;
import kig.dashboard.member.MemberService;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.entity.MemberRole;
import kig.dashboard.post.Post;
import kig.dashboard.post.PostRepository;
import kig.dashboard.post.dto.PostSaveDTO;
import kig.dashboard.post.exception.PostException;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager em;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    JwtService jwtService;

    ObjectMapper objectMapper = new ObjectMapper();

    final String USERNAME = "username";

    private static Member member;

    @BeforeEach
    private void signUpAndAUthentication() {

        Member member = memberRepository.save(Member.builder().username(USERNAME)
                .password("1234")
                .nickname("test1").role(MemberRole.USER).build());
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME)
                                .password("1234")
                                .roles(MemberRole.USER.toString())
                                .build(), null)
        );

        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    private void clear() {
        em.flush();
        em.clear();
    }

    private String getAccessToken() {
        return jwtService.createdAccessToken(USERNAME);
    }

    private String getNoAuthAccessToken() {
        return jwtService.createdAccessToken(USERNAME + 12);
    }

    private Long savePost() {
        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content, Optional.empty());

        Post save = postRepository.save(postSaveDTO.toEntity());
        clear();
        return save.getId();
    }

    private Long saveComment() {
        CommentSaveDTO commentSaveDTO = new CommentSaveDTO("댓글");
        commentService.save(savePost(), commentSaveDTO);
        clear();

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        return resultList.get(0).getId();
    }

    private Long saveRecomment(Long parentId) {

        CommentSaveDTO commentSaveDTO = new CommentSaveDTO("자식댓글");
        commentService.saveReComment(savePost(), parentId, commentSaveDTO);
        clear();

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate ", Comment.class).getResultList();
        return resultList.get(0).getId();
    }

    @Test
    public void 댓글저장_성공() throws Exception {

        Long postId = savePost();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "comment");

        mockMvc.perform(
                post("/comment/" + postId).
                        header("Authorization", "Bearer " + getAccessToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isCreated());

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        assertThat(resultList.size()).isEqualTo(1);
    }

    @Test
    public void 자식댓글저장_성공() throws Exception {

        Long postId = savePost();
        Long parentId = saveComment();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "recomment");

        mockMvc.perform(
                post("/comment/" + postId + "/" + parentId)
                        .header("Authorization", "Bearer " + getAccessToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .params(map)
        ).andExpect(status().isCreated());

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc", Comment.class).getResultList();
        assertThat(resultList.size()).isEqualTo(2);
    }

    @Test
    public void 댓글저장_실패_게시글없음() throws Exception {

        Long postId = savePost();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "comment");

        mockMvc.perform(
                        post("/comment/" + 1000)
                                .header("Authorization", "Bearer " + getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map)
                )
                .andExpect(status().isNotFound());

    }
}