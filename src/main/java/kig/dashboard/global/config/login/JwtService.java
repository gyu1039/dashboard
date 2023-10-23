package kig.dashboard.global.config.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import kig.dashboard.member.MemberRepository;
import kig.dashboard.member.entity.Member;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
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

    public String createdAccessToken(String username) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationTime * 1000))
                .withClaim(USERNAME_CLAIM, username)
                .sign(Algorithm.HMAC512(secret));
    }

    public String createRefreshToken() {
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationTime * 1000))
                .sign(Algorithm.HMAC512(secret));
    }

    public void updateRefreshToken(String username, String refreshToken) {

        memberRepository.findByUsername(username).ifPresent(member -> member.updateRefreshToken(refreshToken));
    }

    public void destroyRefreshToken(String username) {

        memberRepository.findByUsername(username).ifPresent(Member::destroyRefreshToken);
    }

    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {

//        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);

        /*Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
        tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);*/

//        String token = objectMapper.writeValueAsString(tokenMap);
//        response.getWriter().write(token);
    }

    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);

        /*Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);*/
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    public Optional<String> extractUsername(String accessToken) {

        return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getClaim(USERNAME_CLAIM).asString());
    }


    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }


    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
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
