package kig.dashboard.global.config.login.handler;

import kig.dashboard.member.entity.Member;
import kig.dashboard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String refreshToken = request.getParameter("refresh");
        Optional<Member> byRefreshToken = memberRepository.findByRefreshToken(refreshToken);
        byRefreshToken.ifPresent(Member::destroyRefreshToken);
    }
}
