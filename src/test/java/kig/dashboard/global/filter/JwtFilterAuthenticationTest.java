package kig.dashboard.global.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import kig.dashboard.global.config.login.JwtService;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JwtFilterAuthenticationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    JwtService jwtService;

    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static String KEY_USERNAME = "username";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "username";
    private static String PASSWORD = "123456789";
    private static String LOGIN_URL = "/login";

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String BEARER = "Bearer ";

    private ObjectMapper objectMapper = new ObjectMapper();

    private void clear() {
        entityManager.flush();
        entityManager.clear();
    }

    @BeforeEach
    private void init() {
        memberRepository.save(
                Member.builder().username(USERNAME).password(passwordEncoder.encode(PASSWORD))
                        .nickname("test")
                        .build()
        );

        clear();
    }

    private Map<String, String> getUsernamePasswordMap(String username, String password) {

        Map<String, String> map = new HashMap<>();
        map.put(KEY_PASSWORD, password);
        map.put(KEY_USERNAME, username);
        return map;
    }

    private Map<String, String> getAccessAndRefreshToken() throws Exception {

        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);

        MvcResult result = mockMvc.perform(
                post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map))
        ).andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);
        String refreshToken = result.getResponse().getHeader(refreshHeader);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(accessHeader, accessToken);
        tokenMap.put(refreshHeader, refreshToken);
        return tokenMap;
    }


    @Test
    public void 모든토큰존재X() throws Exception {

        mockMvc.perform(
                get("/post")).andExpect(status().isForbidden());
    }

    @Test
    public void 유효한액세스토큰만존재() throws Exception {

        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);

        mockMvc.perform(
                        get("/tmp").header(accessHeader, BEARER + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void 유효하지않은_액세스토큰만존재() throws Exception {

        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);

        mockMvc.perform(
                get("/tmp").header(accessHeader, accessToken + "1")
        ).andExpect(status().isForbidden());

    }

    @Test
    public void 유효한리프레시토큰으로_액세스토큰재발급() throws Exception {

        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken = accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(
                get("/tmp").header(refreshHeader, BEARER + refreshToken)
        ).andExpect(status().isNotFound()).andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);
        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getSubject();
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    @Test
    public void 유효하지않은리프레시토큰만존재() throws Exception {
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken = accessAndRefreshToken.get(refreshHeader);

        mockMvc.perform(
                get("/tmp").header(refreshHeader, refreshToken)
        ).andExpect(status().isForbidden());

        mockMvc.perform(
                get("/tmp").header(refreshHeader, BEARER + refreshToken + "1")
        ).andExpect(status().isForbidden());

    }

    @Test
    public void 토큰들이모두유효할때_액세스토큰재발급() throws Exception {

        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(get("/tmp").header(refreshHeader, BEARER + refreshToken).header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken).getSubject();
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);

        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);
        assertThat(responseRefreshToken).isNull();

    }

    @Test
    public void 유효하지않은액세스토큰_유효한리프레시토큰() throws Exception {

        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= accessAndRefreshToken.get(accessHeader);
        String refreshToken= accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(
                        get("/tmp").header(refreshHeader, BEARER + refreshToken).header(accessHeader, BEARER + accessToken + "1")
                ).andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken).getSubject();

        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
        assertThat(responseRefreshToken).isNull();
    }

    @Test
    public void 유효하지않은리프레시토큰_유효한액세스토큰() throws Exception {

        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= accessAndRefreshToken.get(accessHeader);
        String refreshToken= accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(get("/tmp")
                        .header(refreshHeader, BEARER + refreshToken+1)
                        .header(accessHeader, BEARER + accessToken ))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        assertThat(responseAccessToken).isNull();
        assertThat(responseRefreshToken).isNull();

    }

    @Test
    public void 토큰들이_모두_유효하지않을때() throws Exception {

        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= accessAndRefreshToken.get(accessHeader);
        String refreshToken= accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(get("/tmp").header(refreshHeader, BEARER + refreshToken + 1).header(accessHeader, BEARER + accessToken + 1))
                .andExpect(status().isForbidden())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken= result.getResponse().getHeader(refreshHeader);

        assertThat(responseAccessToken).isNull();
        assertThat(responseRefreshToken).isNull();
    }

    @Test
    public void request_login_url() throws Exception {

        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= accessAndRefreshToken.get(accessHeader);
        String refreshToken= accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(
                        post(LOGIN_URL).header(refreshHeader, BEARER + refreshToken).header(accessHeader, BEARER + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn();

//        assertThat(result.getResponse().getContentAsString()).isEqualTo
    }
}