package kig.dashboard.global.config.login.filter;

import kig.dashboard.global.config.login.JwtService;
import kig.dashboard.global.config.login.SecurityUtil;
import kig.dashboard.member.entity.Member;
import kig.dashboard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;


    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("{}", "사용자 권한을 확인합니다");
        log.info("requestUrl {}", request.getRequestURI());

        String accessToken = request.getHeader("Authorization").replace(BEARER, "");
        String refreshToken = request.getHeader("Authorization-refresh").replace(BEARER, "");

        boolean isAccessTokenValid = jwtService.isTokenValid(accessToken);
        boolean isRefreshTokenValid = jwtService.isTokenValid(refreshToken);

        log.info("accessToken : {}, refreshToken: {}", accessToken, refreshToken);
        log.info("isAccessTokenValid: {}, isRefreshTokenValid: {}", isAccessTokenValid, isRefreshTokenValid);

        Optional<Member> optionalMember = memberRepository.findByRefreshToken(refreshToken);

        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();

            if(isAccessTokenValid && isRefreshTokenValid) {

                String accessToken1 = jwtService.createAccessToken(member.getUsername());
                addTokenToHeader(response, accessToken1, refreshToken, member);

            } else if(isAccessTokenValid) {
                addTokenToHeader(response, accessToken, refreshToken, member);

            } else if(isRefreshTokenValid) {

                String accessToken1 = jwtService.createAccessToken(member.getUsername());
                addTokenToHeader(response, accessToken1, refreshToken, member);

            }
        }


        filterChain.doFilter(request, response);
    }

    private void addTokenToHeader(HttpServletResponse response, String accessToken1, String refreshToken, Member member) throws IOException {

        jwtService.addTokenToHeader(response, accessToken1, refreshToken, member);

        log.info("JwtAuthorizationFilter.addTokenToHeader 실행");
        UserDetails details = User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}
