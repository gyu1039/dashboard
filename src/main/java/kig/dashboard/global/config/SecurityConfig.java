package kig.dashboard.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kig.dashboard.global.login.filter.JsonUsernamePasswordAuthenticationFilter;
import kig.dashboard.global.login.handler.LoginFailureHandler;
import kig.dashboard.global.login.handler.LoginSuccessJWTProvideHandler;
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

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/login", "/signUp", "/").permitAll()
                    .anyRequest().authenticated();

        http.addFilterAfter(jsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);

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
        return new LoginSuccessJWTProvideHandler();
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }



    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter() {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter
                = new JsonUsernamePasswordAuthenticationFilter(objectMapper);

        jsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessJWTProviderHandler());
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return jsonUsernamePasswordAuthenticationFilter;
    }


}
