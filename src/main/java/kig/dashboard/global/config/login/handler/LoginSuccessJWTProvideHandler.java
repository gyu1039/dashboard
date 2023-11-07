package kig.dashboard.global.config.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kig.dashboard.global.config.login.JwtService;
import kig.dashboard.member.dto.MemberAuthorizationDTO;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        log.info("LoginSuccessJWTProvideHandler 실행 ");
        String username = extractUsername(authentication);
        log.info("username : {}",username);
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        Member member = memberRepository.findByUsername(username).get();
        member.setRefreshToken(refreshToken);
        jwtService.addTokenToBody(response, accessToken, refreshToken);


        MemberAuthorizationDTO memberDTO = new MemberAuthorizationDTO();
        memberDTO.setRole("임시");
        memberDTO.setUsername(member.getUsername());


        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(memberDTO));
    }

    private String extractUsername(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return principal.getUsername();
    }
}
