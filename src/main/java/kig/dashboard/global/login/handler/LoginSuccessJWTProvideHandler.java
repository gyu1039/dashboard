package kig.dashboard.global.login.handler;

import kig.dashboard.global.jwt.service.JwtService;
import kig.dashboard.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String username = extractUsername(authentication);
        String accessToken = jwtService.createdAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        memberRepository.findByUsername(username).ifPresent(
                member -> member.updateRefreshToken(refreshToken)
        );

        log.info("로그인에 성공합니다. username: {}", username);
        log.info("AccessToken을 발급합니다. AccessToken: {}", accessToken);
        log.info("RefreshToken을 발급합니다. RefreshToken: {}", refreshToken);
    }

    private String extractUsername(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return principal.getUsername();
    }
}
