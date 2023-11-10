package kig.dashboard.global.config.login.handler;

import kig.dashboard.global.config.login.JwtService;
import kig.dashboard.global.config.login.SecurityUtil;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.repository.MemberRepository;
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

        log.info("LoginSuccessJWTProvideHandler 실행 ");
        String username = SecurityUtil.getLoginUsername();
        Member member = memberRepository.findByUsername(username).get();

        log.info("{}", member);

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken(member.getId());

        log.info("accessToken: {}, refreshToken: {}", accessToken, refreshToken);
        member.setRefreshToken(refreshToken);

        jwtService.addTokenToHeader(response, accessToken, refreshToken, member);

    }

}
