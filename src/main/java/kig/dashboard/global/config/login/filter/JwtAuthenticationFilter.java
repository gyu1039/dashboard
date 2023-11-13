package kig.dashboard.global.config.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import kig.dashboard.member.dto.MemberLoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/api/login";
    private static final String HTTP_METHOD = "POST";
    private static final String CONTENT_TYPE = "application/json; charset=UTF-8";


    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCH
            = new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD);


    public JwtAuthenticationFilter() {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCH);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        MemberLoginDTO member = objectMapper.readValue(messageBody, MemberLoginDTO.class);

        log.info("{}", member);

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword());

        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(authenticationToken);

        return this.getAuthenticationManager().authenticate(authenticationToken);
    }
}
