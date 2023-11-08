package kig.dashboard.global.config.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.member.entity.Member;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor @Setter(value = AccessLevel.PRIVATE)
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpirationTime;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationTime;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;


    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer ";

    private final MemberRepository memberRepository;

    public String createAccessToken(String username) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .withClaim(USERNAME_CLAIM, username)
                .sign(Algorithm.HMAC512(secret));
    }

    public String createRefreshToken() {
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .sign(Algorithm.HMAC512(secret));
    }

    public void destroyRefreshToken(String username) {
        memberRepository.findByUsername(username).ifPresent(Member::destroyRefreshToken);
    }


    public void addTokenToBody(HttpServletResponse response, String accessToken, String refreshToken, Member member) throws IOException {

        response.setStatus(HttpServletResponse.SC_OK);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        map.put("access", accessToken);
        map.put("refresh", refreshToken);
        map.put("role", member.getRole().name());
        response.getWriter().write(objectMapper.writeValueAsString(map));
    }


    public String extractUsername(String accessToken) {

        return JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getClaim(USERNAME_CLAIM).asString();
    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 Token입니다.{}", e.getMessage());
            return false;
        }

    }
}
