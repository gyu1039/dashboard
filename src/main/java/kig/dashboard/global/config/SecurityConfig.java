package kig.dashboard.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kig.dashboard.global.config.login.JwtService;
import kig.dashboard.global.config.login.filter.JsonUsernamePasswordAuthenticationFilter;
import kig.dashboard.global.config.login.filter.JwtAuthenticationProcessingFilter;
import kig.dashboard.global.config.login.handler.LoginFailureHandler;
import kig.dashboard.global.config.login.handler.LoginSuccessJWTProvideHandler;
import kig.dashboard.member.MemberRepository;
import kig.dashboard.member.login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(corsFilter)
                .authorizeRequests()
                .antMatchers("/login", "/signup", "/").permitAll()
//                .antMatchers("/todo1").hasRole("ROLE_TEAM1")
//                .antMatchers("/todo2").hasRole("ROLE_TEAM2")
//                .antMatchers("/todo3").hasRole("ROLE_TEAM3")
                .anyRequest().authenticated();

        http.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class);
        http.addFilterAfter(jwtAuthenticationProcessingFilter(), JsonUsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(loginService);
        return new ProviderManager(provider);
    }


    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProviderHandler() {
        return new LoginSuccessJWTProvideHandler(jwtService, memberRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter
                = new JsonUsernamePasswordAuthenticationFilter(objectMapper);

        jsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessJWTProviderHandler());
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return jsonUsernamePasswordAuthenticationFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationProcessingFilter(jwtService, memberRepository);
    }


}
