package kig.dashboard.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import kig.dashboard.member.dto.MemberSignUpDTO;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.exception.MemberException;
import kig.dashboard.member.exception.MemberExceptionType;
import kig.dashboard.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EntityManager em;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    PasswordEncoder passwordEncoder;

    private static String SIGN_UP_URL = "/signup";

    private String username = "username";
    private String password = "password1234@";
    private String nickname = "test";

    private void clear() {
        em.flush();
        em.clear();
    }

    private void signUp(String signUpData) throws Exception {
        mockMvc.perform(
                post(SIGN_UP_URL).contentType(MediaType.APPLICATION_JSON).content(signUpData)
        ).andExpect(status().isOk());

        clear();
    }

    @Value("${jwt.access.header}")
    private String accessHeader;

    private static final String BEARER = "Bearer ";

    private String getAccessToken() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);

        MvcResult result = mockMvc.perform(
                post("/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(map))
        ).andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(accessHeader);

    }

    @Test
    public void 회원가입성공() throws Exception {

        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));

        signUp(signUpData);

        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다."));
        assertThat(member.getNickname()).isEqualTo(nickname);
    }

    @Test
    public void 회원가입실패_필드없음() throws Exception {

        String noUsername = objectMapper.writeValueAsString(new MemberSignUpDTO(null, password, nickname));
        String noPassword = objectMapper.writeValueAsString(new MemberSignUpDTO(username, null, nickname));
        String noNickname = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, null));

        signUpFail(noUsername);
        signUpFail(noPassword);
        signUpFail(noNickname);

        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

    private void signUpFail(String signUpData) throws Exception {
        mockMvc.perform(
                        post(SIGN_UP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 회원정보수정_성공() throws Exception {

        String signupData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        String signupData2 = objectMapper.writeValueAsString(new MemberSignUpDTO(username + 1, password, nickname));

        signUp(signupData);
        signUp(signupData2);

        assertThat(memberRepository.findAll().size()).isEqualTo(2);

        String accessToken = getAccessToken();
        HashMap<String, Object> map = new HashMap<>();
        map.put("nickname", nickname + " 변경");
        String updatedData = objectMapper.writeValueAsString(map);

        mockMvc.perform(
                put("/member").header(accessHeader, BEARER + accessToken).contentType(MediaType.APPLICATION_JSON).content(updatedData)
        ).andExpect(status().isOk());

        Member member = memberRepository.findByUsername(username).orElseThrow();
        assertThat(member.getNickname()).isEqualTo(nickname + " 변경");

        Member member2 = memberRepository.findByUsername(username + 1).orElseThrow();
        assertThat(member2.getNickname()).isEqualTo(nickname);
    }


    @Test
    public void 비밀번호수정_성공() throws Exception {

        String data = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        signUp(data);

        String accessToken = getAccessToken();

        HashMap<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);
        map.put("toBePassword", password + "변경했어");

        String updated = objectMapper.writeValueAsString(map);

        mockMvc.perform(
                put("/member/password").header(accessHeader, BEARER + accessToken).contentType(MediaType.APPLICATION_JSON).content(updated)
        ).andExpect(status().isOk());

        Member member = memberRepository.findByUsername(username).orElseThrow();
        assertThat(passwordEncoder.matches(password, member.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(password+"변경했어", member.getPassword())).isTrue();
    }

    @Test
    public void 비밀번호수정_실패_검증비밀번호틀림() throws Exception {

        String data = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        signUp(data);

        String accessToken = getAccessToken();

        HashMap<String, Object> map = new HashMap<>();

        map.put("checkPassword", password + "1");
        map.put("toBePassword", password + "123");

        String updatePassword = objectMapper.writeValueAsString(map);


        mockMvc.perform(
                put("/member/password")
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePassword)
        ).andExpect(status().isBadRequest());

        Member member = memberRepository.findByUsername(username).orElseThrow();
        assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(password + "변경했나?", member.getPassword())).isFalse();


    }

    @Test
    public void 비밀번호수정_실패_비밀번호형식이맞지않음() throws Exception {

        String data = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        signUp(data);

        String accessToken = getAccessToken();

        HashMap<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);
        map.put("toBoPassword", 12345);

        String updatePassword = objectMapper.writeValueAsString(map);

        mockMvc.perform(
                        post("/member/password")
                                .header(accessHeader, BEARER + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword)
                )
                .andExpect(status().isBadRequest());

        Member member = memberRepository.findByUsername(username).orElseThrow();
        assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("12345", member.getPassword())).isFalse();
    }

    @Test
    public void 회원탈퇴_성공() throws Exception {
        String data = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        signUp(data);

        String accessToken = getAccessToken();

        HashMap<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);

        String password = objectMapper.writeValueAsString(map);

        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader, BEARER + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(password)
                )
                .andExpect(status().isOk());

        assertThrows(Exception.class, () -> memberRepository.findByUsername(username).orElseThrow());
    }

    @Test
    public void 회원탈퇴_실패_비밀번호틀림() throws Exception {
        String data = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        signUp(data);

        String accessToken = getAccessToken();

        HashMap<String, Object> map = new HashMap<>();
        map.put("checkPassword", password + 123);

        String password = objectMapper.writeValueAsString(map);

        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader, BEARER + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(password)
                )
                .andExpect(status().isBadRequest());

        Member member = memberRepository.findByUsername(username).orElseThrow();
        assertThat(member).isNotNull();
    }

    @Test
    public void 회원탈퇴_실패_권한없음() throws Exception {

        String data = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        signUp(data);

        String accessToken = getAccessToken();

        HashMap<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);

        String password = objectMapper.writeValueAsString(map);

        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader, BEARER + accessToken + "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(password)
                )
                .andExpect(status().isForbidden());

        Member member = memberRepository.findByUsername(username).orElseThrow();
        assertThat(member).isNotNull();
    }

    @Test
    public void 회원조회() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Long id = memberRepository.findAll().get(0).getId();

        MvcResult result = mockMvc.perform(
                get("/member/" + id)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER + accessToken)
        ).andExpect(status().isOk()).andReturn();


        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        assertThat(member.getUsername()).isEqualTo(map.get("username"));
    }

    @Test
    public void 회원조회실패_없는회원조회() throws Exception {

        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        signUp(signUpData);

        String accessToken = getAccessToken();

        MvcResult result = mockMvc.perform(
                get("/member/" + 123)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER + accessToken)
        ).andExpect(status().isNotFound()).andReturn();

        Map<String, Integer> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(map.get("errorCode")).isEqualTo(MemberExceptionType.NOT_FOUND_MEMBER.getErrorCode());
    }

    @Test
    public void 회원조회실패_토큰없음() throws Exception {

        String signUpData = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        signUp(signUpData);

        mockMvc.perform(
                get("/member/" + 123)
                        .characterEncoding(StandardCharsets.UTF_8)
        ).andExpect(status().isForbidden());
    }


    @Test
    public void 내정보조회_성공() throws Exception {

        String data = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        signUp(data);

        String accessToken = getAccessToken();


        MvcResult result = mockMvc.perform(
                        get("/member")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken)
                )
                .andExpect(status().isOk()).andReturn();

        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Member member = memberRepository.findByUsername(username).orElseThrow();

        assertThat(member.getUsername()).isEqualTo(map.get("username"));
        assertThat(member.getNickname()).isEqualTo(map.get("nickname"));



    }

    @Test
    public void 내정보조회_실패_토큰없음() throws Exception {

        String data = objectMapper.writeValueAsString(new MemberSignUpDTO(username, password, nickname));
        signUp(data);

        String accessToken = getAccessToken();

            mockMvc.perform(
                        get("/member")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken + 1)
                )
                .andExpect(status().isForbidden());
    }
}