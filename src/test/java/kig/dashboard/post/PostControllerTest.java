package kig.dashboard.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import kig.dashboard.global.config.login.JwtService;
import kig.dashboard.member.MemberRepository;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.entity.MemberRole;
import kig.dashboard.post.cond.PostSearchCondition;
import kig.dashboard.post.dto.PostInfoDTO;
import kig.dashboard.post.dto.PostPagingDTO;
import kig.dashboard.post.file.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    final String USERNAME = "username1";

    private static Member member;

    private void clear() {
        em.flush();
        em.clear();
    }

    @BeforeEach
    public void signUpMember() {
        member = memberRepository.save(Member.builder().username(USERNAME).password("1234").nickname("test1").role(MemberRole.USER).build());
        clear();
    }

    private String getAccessToken() {
        return jwtService.createAccessToken(USERNAME);
    }

    private MockMultipartFile getMockMultipartFile() throws IOException {
        return new MockMultipartFile("uploadFile", "goo.png", "image/png", new FileInputStream("C:\\Users\\Administrator\\Desktop\\tmp\\goo.png"));
    }

    @Test
    public void 게시글저장_성공() throws Exception {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("title", "제목");
        map.add("content", "내용");

        mockMvc.perform(
                        post("/post")
                                .header("Authorization", "Bearer " + getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isCreated());

        assertThat(postRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void 게시글저장_실패_제목또는내용없음() throws Exception {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("title", "제목");

        mockMvc.perform(
                post("/post")
                        .header("Authorization", "Bearer " + getAccessToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA).params(map)
        ).andExpect(status().isBadRequest());

        map = new LinkedMultiValueMap<>();
        map.add("content", "내용");

        mockMvc.perform(
                post("/post")
                        .header("Authorization", "Bearer " + getAccessToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA).params(map)
        ).andExpect(status().isBadRequest());

    }

    @Test
    public void 게시글_수정_제목변경_성공() throws Exception {

        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(member);
        Post savedPost = postRepository.save(post);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_TITLe = "제목";
        map.add("title", UPDATE_TITLe);

        mockMvc.perform(
                put("/post/" + savedPost.getId()).header("Authorization", "Bearer " + getAccessToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA).params(map)
        ).andExpect(status().isOk());

        Post post1 = postRepository.findAll().get(0);
        assertThat(post1.getContent()).isEqualTo("수정전내용");
        assertThat(post1.getTitle()).isEqualTo(UPDATE_TITLe);
    }

    @Test
    public void 게시글_수정_내용변경_성공() throws Exception {

        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(member);
        Post savedPost = postRepository.save(post);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_CONTENT = "내용";
        map.add("content", UPDATE_CONTENT);

        mockMvc.perform(
                put("/post/" + savedPost.getId()).header("Authorization", "Bearer " + getAccessToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA).params(map)
        ).andExpect(status().isOk());

        Post post1 = postRepository.findAll().get(0);
        assertThat(post1.getContent()).isEqualTo(UPDATE_CONTENT);
        assertThat(post1.getTitle()).isEqualTo("수정전제목");
    }

    @Test
    public void 게시글_수정_모두_성공() throws Exception {

        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(member);
        Post save = postRepository.save(post);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_TITLE = "내용";
        final String UPDATE_CONTENT = "내용";
        map.add("title", UPDATE_TITLE);
        map.add("content", UPDATE_CONTENT);

        mockMvc.perform(
                put("/post/" + save.getId()).header("Authorization", "Bearer " + getAccessToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA).params(map)
        ).andExpect(status().isOk());

        assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo(UPDATE_TITLE);
        assertThat(postRepository.findAll().get(0).getContent()).isEqualTo(UPDATE_CONTENT);
    }

    @Test
    public void 게시글_수정_업로드파일추가() throws Exception {

        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(member);
        Post save = postRepository.save(post);

        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/post/" + save.getId());


        requestBuilder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(
                        requestBuilder
                                .file(getMockMultipartFile())
                                .header("Authorization", "Bearer " + getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());


        String filePath = postRepository.findAll().get(0).getFilePath();
        assertThat(filePath).isNotNull();

    }



    @Autowired
    private FileService fileService;

    @Test
    public void 게시글수정_업로드파일제거_성공() throws Exception {

        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(member);
        String path = fileService.save(getMockMultipartFile());
        post.updateFilePath(path);

        Post savedPost = postRepository.save(post);

        assertThat(postRepository.findAll().get(0).getFilePath()).isNotNull();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String updateTitle = "제목";
        final String updateContent = "내용";

        map.add("title", updateTitle);
        map.add("content", updateContent);

        mockMvc.perform(
                        put("/post/"+savedPost.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isOk());

        assertThat(postRepository.findAll().get(0).getContent()).isEqualTo(updateContent);
        assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo(updateTitle);
        assertThat(postRepository.findAll().get(0).getFilePath()).isNull();

    }

    @Test
    public void 게시글_수정_실패_권한없음() throws Exception {

        Member newMember = memberRepository.save(Member.builder().username("newMEmber1t123").password("1234").nickname("123").role(MemberRole.USER).build());
        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(newMember);
        Post savePost = postRepository.save(post);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_CONTENT = "내용";
        final String UPDATE_TITlE = "제목";
        map.add("title", UPDATE_TITlE);
        map.add("content", UPDATE_CONTENT);


        mockMvc.perform(
                        put("/post/"+savePost.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isForbidden());



        assertThat(postRepository.findAll().get(0).getContent()).isEqualTo("수정전내용");
        assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo("수정전제목");
    }

    @Test
    public void 게시글_삭제_성공() throws Exception {

        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(member);
        Post savePost = postRepository.save(post);


        mockMvc.perform(
                delete("/post/"+savePost.getId())
                        .header("Authorization", "Bearer "+ getAccessToken())
        ).andExpect(status().isOk());



        assertThat(postRepository.findAll().size()).isEqualTo(0);

    }

    @Test
    public void 게시글_삭제_실패_권한없음() throws Exception {
        //given
        Member newMember = memberRepository.save(Member.builder().username("newMEmber1t123").password("1234").nickname("123").role(MemberRole.USER).build());
        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(newMember);
        Post savePost = postRepository.save(post);

        //when
        mockMvc.perform(
                delete("/post/"+savePost.getId())
                        .header("Authorization", "Bearer "+ getAccessToken())
        ).andExpect(status().isForbidden());


        //then
        assertThat(postRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    public void 게시글_조회() throws Exception {

        Member newMember = memberRepository.save(Member.builder().username("newMEmber1t123").password("1234").nickname("123").role(MemberRole.USER).build());
        Post post = Post.builder().title("title").content("content").build();
        post.confirmWriter(newMember);
        Post savePost = postRepository.save(post);

        MvcResult result = mockMvc.perform(
                get("/post/" + savePost.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + getAccessToken())
        ).andExpect(status().isOk()).andReturn();

        PostInfoDTO postInfoDto = objectMapper.readValue(result.getResponse().getContentAsString(), PostInfoDTO.class);

        assertThat(postInfoDto.getPostId()).isEqualTo(post.getId());
        assertThat(postInfoDto.getContent()).isEqualTo(post.getContent());
        assertThat(postInfoDto.getTitle()).isEqualTo(post.getTitle());

    }

    @Value("${spring.data.web.pageable.default-page-size}")
    private int pageCount;

   /* @Test
    public void 게시글_검색() throws Exception {

        Member newMember = memberRepository.save(Member.builder().username("newMEmber1123").password("!23123124421").nickname("123").role(MemberRole.USER).build());

        final int POST_COUNT = 50;
        for(int i = 1; i<= POST_COUNT; i++ ){
            Post post = Post.builder().title("title"+ i).content("content"+i).build();
            post.confirmWriter(newMember);
            postRepository.save(post);
        }
        clear();


        MvcResult result = mockMvc.perform(
                get("/post")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + getAccessToken())
        ).andExpect(status().isOk()).andReturn();


        PostPagingDTO postList = objectMapper.readValue(result.getResponse().getContentAsString(), PostPagingDTO.class);

        assertThat(postList.getTotalElementCount()).isEqualTo(POST_COUNT);
        assertThat(postList.getCurrentPageElementCount()).isEqualTo(pageCount);
        assertThat(postList.getSimpleDTOList().get(0).getContent()).isEqualTo("content50");

    }*/
}