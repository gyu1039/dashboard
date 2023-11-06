package kig.dashboard.global.config;

import kig.dashboard.global.config.login.JwtService;
import kig.dashboard.global.config.login.filter.JwtAuthenticationFilter;
import kig.dashboard.global.config.login.filter.JwtAuthorizationFilter;
import kig.dashboard.global.config.login.handler.LoginFailureHandler;
import kig.dashboard.global.config.login.handler.LoginSuccessJWTProvideHandler;
import kig.dashboard.member.repository.MemberRepository;
import kig.dashboard.global.config.login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final CorsConfig corsConfig;
    private final LoginService loginService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .addFilter(corsConfig.corsFilter())
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthorizationFilter(), JwtAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/login", "/api/signup", "/api/checkid/**").permitAll()
                .anyRequest().authenticated()
        ;

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter();

        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager());
        jwtAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessJWTProviderHandler());
        jwtAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());

        return jwtAuthenticationFilter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtService, memberRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(loginService);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
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
    public FilterRegistrationBean<OpenEntityManagerInViewFilter> registerOpenEntityManagerInViewFilter() {
        FilterRegistrationBean<OpenEntityManagerInViewFilter> filterRegistrationBean
                = new FilterRegistrationBean<>();

        filterRegistrationBean.setFilter(new OpenEntityManagerInViewFilter());
        filterRegistrationBean.setOrder(Integer.MIN_VALUE);
        return filterRegistrationBean;
    }
}
