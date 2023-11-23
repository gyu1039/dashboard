package kig.dashboard.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import kig.dashboard.comment.dto.CommentInfoDTO;
import kig.dashboard.comment.dto.CommentSaveDTO;
import kig.dashboard.comment.exception.CommentException;
import kig.dashboard.comment.exception.CommentExceptionType;
import kig.dashboard.global.config.login.JwtService;
import kig.dashboard.member.MemberRole;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.MemberService;
import kig.dashboard.member.entity.Member;
import kig.dashboard.post.entity.Post;
import kig.dashboard.post.repository.PostRepository;
import kig.dashboard.post.dto.PostSaveDTO;
import kig.dashboard.post.exception.PostException;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
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

        member = memberRepository.save(Member.builder().username(USERNAME)
                .password("1234")
                .nickname("test1").build());
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        UserDetails build = User.builder()
                .username(USERNAME)
                .password("1234")
                .roles(MemberRole.USER.name())
                .build();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        build, build.getAuthorities())
        );

        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    private void clear() {
        em.flush();
        em.clear();
    }

    private String getAccessToken() {
        return jwtService.createAccessToken(USERNAME);
    }

    private String getNoAuthAccessToken() {
        return jwtService.createAccessToken(USERNAME + 12);
    }

    private Long savePost() {
        String title = "제목";
        String content = "내용";
        PostSaveDTO postSaveDTO = new PostSaveDTO(title, content);

        Post save = postRepository.save(postSaveDTO.toEntity());
        clear();
        return save.getId();
    }

    private Long saveComment(Long postId) {
        CommentSaveDTO commentSaveDTO = new CommentSaveDTO("댓글");

        commentService.save(postId, commentSaveDTO);
        clear();

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc", Comment.class).getResultList();
        return resultList.get(0).getId();
    }

    private Long saveRecomment(Long postId, Long parentId) {

        CommentSaveDTO commentSaveDTO = new CommentSaveDTO("자식댓글");
        commentService.saveReComment(postId, parentId, commentSaveDTO);
        clear();

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc", Comment.class).getResultList();
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
        Long parentId = saveComment(postId);

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

    @Test
    public void 자식댓글저장_실패_게시글없음() throws Exception {

        Long postId = savePost();
        Long parentId = saveComment(postId);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "recomment");

        mockMvc.perform(
                post("/commnent/" + 1000 + "/" + parentId)
                        .header("Authorization", "Bearer " + getAccessToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA).params(map)
        ).andExpect(status().isNotFound());

    }

    @Test
    public void 자식댓글저장_실패_부모댓글이_없음() throws Exception {

        Long postId = savePost();
        assertThrows(PostException.class, () -> saveComment(100L));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "recomment");



        mockMvc.perform(
                        post("/comment/"+postId+"/"+10000)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isNotFound());
    }

    @Test
    public void 업데이트_성공() throws Exception {

        Long postId = savePost();
        Long commentId = saveComment(postId);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "updateComment");

        mockMvc.perform(
                        put("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isOk());


        Comment comment = commentRepository.findById(commentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("updateComment");

    }

    @Test
    public void 업데이트_실패_권한이없음() throws Exception {

        Long postId = savePost();
        Long commentId = saveComment(postId);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "updateComment");


        mockMvc.perform(
                        put("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getNoAuthAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isForbidden());




        Comment comment = commentRepository.findById(commentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("댓글");
    }

    @Test
    public void 댓글삭제_실패_권한이_없음() throws Exception {

        Long postId = savePost();
        Long commentId = saveComment(postId);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", "updateComment");


        mockMvc.perform(
                        delete("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getNoAuthAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isForbidden());



        Comment comment = commentRepository.findById(commentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("댓글");
    }

    @Test
    public void 댓글삭제_자식댓글이_남아있는_경우() throws Exception {

        Long postId = savePost();
        Long commentId = saveComment(postId);
        saveRecomment(postId,commentId);
        saveRecomment(postId,commentId);
        saveRecomment(postId,commentId);
        saveRecomment(postId,commentId);

        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(4);


        mockMvc.perform(
                        delete("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());


        Comment ret = commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));
        CommentInfoDTO findComment = new CommentInfoDTO(ret, ret.getChildList());
        assertThat(findComment).isNotNull();
        assertThat(findComment.isRemoved()).isTrue();
        assertThat(findComment.getReCommentInfoDTOList().size()).isEqualTo(4);
        assertThat(findComment.getContent()).isEqualTo(findComment.DEFAULT_DELETED_MESSAGE);
    }

    @Test
    public void 댓글삭제_자식댓글이_없는_경우() throws Exception {

        Long postId = savePost();
        Long commentId = saveComment(postId);

        mockMvc.perform(
                        delete("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());
        clear();

        assertThat(commentRepository.findAll().size()).isSameAs(0);
        assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)))
                .getExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT);
    }

    @Test
    public void 부모댓글삭제_자식댓글이모두_삭제된경우() throws Exception {

        Long postId = savePost();
        Long parentId = saveComment(postId);
        Long reCommend1Id = saveRecomment(postId, parentId);
        Long reCommend2Id = saveRecomment(postId, parentId);
        Long reCommend3Id = saveRecomment(postId, parentId);
        Long reCommend4Id = saveRecomment(postId, parentId);

        assertThat(commentRepository.findById(parentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))
                .getChildList().size()).isEqualTo(4);


        commentService.remove(reCommend1Id);
        commentService.remove(reCommend2Id);
        commentService.remove(reCommend3Id);
        commentService.remove(reCommend4Id);

        assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        assertThat(commentRepository.findById(reCommend2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        assertThat(commentRepository.findById(reCommend3Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        assertThat(commentRepository.findById(reCommend4Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();

        mockMvc.perform(
                        delete("/comment/" + parentId)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());
        clear();


        assertThat(commentRepository.findAll().size()).isEqualTo(0);
        LongStream.rangeClosed(parentId, reCommend4Id).forEach(id ->
                assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)))
                        .getExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT)
        );


//        assertThrows(CommentException.class, () -> commentRepository.findById(parentId).orElseThrow());
//        assertThrows(CommentException.class, () -> commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)));



    }

    @Test
    public void 자식댓글삭제_부모댓글이_남아있는_경우() throws Exception {

        Long postId = savePost();
        Long commentId = saveComment(postId);
        Long reCommend1Id = saveRecomment(postId, commentId);


        mockMvc.perform(
                        delete("/comment/"+reCommend1Id)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());
        clear();

        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isFalse();
        assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
    }

    @Test
    public void 자식댓글삭제_부모댓글과_자식댓글삭제이_모두삭제된경우() throws Exception {

        Long postId = savePost();
        Long commentId = saveComment(postId);
        Long reCommend1Id = saveRecomment(postId, commentId);
        Long reCommend2Id = saveRecomment(postId, commentId);
        Long reCommend3Id = saveRecomment(postId, commentId);


        commentService.remove(reCommend2Id);
        clear();
        commentService.remove(commentId);
        clear();
        commentService.remove(reCommend3Id);
        clear();


        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(3);


        mockMvc.perform(
                        delete("/comment/"+reCommend1Id)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());



        LongStream.rangeClosed(commentId, reCommend3Id).forEach(id ->
                assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(id)
                        .orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).getExceptionType())
                        .isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT)
        );

    }

    @Test
    public void 자식댓글삭제_부모댓글삭제되고_다른자식댓글이남아있는경우() throws Exception {

        Long postId = savePost();
        Long commentId = saveComment(postId);
        Long reCommend1Id = saveRecomment(postId, commentId);
        Long reCommend2Id = saveRecomment(postId, commentId);
        Long reCommend3Id = saveRecomment(postId, commentId);


        commentService.remove(reCommend3Id);
        commentService.remove(commentId);
        clear();

        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(3);


        mockMvc.perform(
                        delete("/comment/"+reCommend2Id)
                                .header("Authorization", "Bearer "+ getAccessToken()))
                .andExpect(status().isOk());


        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();



        assertThat(commentRepository.findById(reCommend2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(reCommend2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getId()).isNotNull();
        assertThat(commentRepository.findById(reCommend3Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getId()).isNotNull();
        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getId()).isNotNull();

    }
}