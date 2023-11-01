package kig.dashboard.global.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import kig.dashboard.global.config.login.JwtService;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.MemberRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Slf4j
class JwtServiceTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer ";

    private String username = "username";

    @BeforeEach
    public void init() {
        Member member = Member.builder().username(username).password("!23456").nickname("test").role(MemberRole.USER).build();
        memberRepository.save(member);
        clear();
    }

    private void clear() {
        entityManager.flush();
        entityManager.close();
    }

    private DecodedJWT getVerify(String token) {
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
    }

    @Test
    public void AccessToken_발급() {

        String accessToken = jwtService.createAccessToken(username);
        DecodedJWT verify = getVerify(accessToken);

        String subject = verify.getSubject();
        String findUsername = verify.getClaim(USERNAME_CLAIM).asString();

        assertThat(findUsername).isEqualTo(username);
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    @Test
    public void RefreshToken_발급() {

        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);

        String subject = verify.getSubject();
        assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);

        String username = verify.getClaim(USERNAME_CLAIM).asString();
        assertThat(username).isNull();
    }

    @Test
    public void updateRefreshToken() throws InterruptedException {

        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();

        Thread.sleep(3000);

        String reIssuedRefreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, reIssuedRefreshToken);
        clear();

        assertThrows(Exception.class, () -> memberRepository.findByRefreshToken(refreshToken).get());
        assertThat(memberRepository.findByRefreshToken(reIssuedRefreshToken).get().getUsername()).isEqualTo(username);
    }

    @Test
    public void destroyRefreshToken() {

        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();

        jwtService.destroyRefreshToken(username);
        clear();

        assertThrows(Exception.class, () -> memberRepository.findByRefreshToken(refreshToken).get());

        Member member = memberRepository.findByUsername(username).get();
        assertThat(member.getRefreshToken()).isNull();
    }

    @Test
    public void setAccessTokenHeader() throws IOException {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        assertThat(headerAccessToken).isEqualTo(accessToken);
    }

    @Test
    public void setRefreshTokenHeader() throws IOException {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        jwtService.setRefreshTokenHeader(mockHttpServletResponse, refreshToken);

        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);
        assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    public void sendToken() throws IOException {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);
        assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }

    private HttpServletRequest setRequest(String accessToken, String refreshToken) throws IOException {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

        mockHttpServletRequest.addHeader(accessHeader, BEARER + headerAccessToken);
        mockHttpServletRequest.addHeader(refreshHeader, BEARER + headerRefreshToken);

        return mockHttpServletRequest;
    }


    @Test
    public void extractAccessToken() throws Exception {

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        String extractAccessToken = jwtService.extractAccessToken(httpServletRequest).orElseThrow(() -> new Exception("토큰이 없습니다"));

        assertThat(extractAccessToken).isEqualTo(accessToken);
        assertThat(getVerify(extractAccessToken).getClaim(USERNAME_CLAIM).asString()).isEqualTo(username);
    }

    @Test
    public void extractRefreshToken() throws Exception {

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        String extractRefreshToken = jwtService.extractRefreshToken(httpServletRequest).orElseThrow(() -> new Exception("토큰이 없습니다"));

        assertThat(extractRefreshToken).isEqualTo(refreshToken);
        assertThat(getVerify(extractRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT);
    }

    @Test
    public void extractUsername() throws Exception {
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);
        String requestAccessToken = jwtService.extractAccessToken(httpServletRequest).orElseThrow(() -> new Exception("토큰이 없습니다"));

        String extractUsername = jwtService.extractUsername(requestAccessToken).orElseThrow(() -> new Exception("토큰이 없습니다"));

        assertThat(extractUsername).isEqualTo(username);
    }

    @Test
    public void validateToken() {

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        assertThat(jwtService.isTokenValid(accessToken)).isTrue();
        assertThat(jwtService.isTokenValid(refreshToken)).isTrue();
        assertThat(jwtService.isTokenValid(accessToken+"d")).isFalse();
        assertThat(jwtService.isTokenValid(refreshToken+"d")).isFalse();

    }

}