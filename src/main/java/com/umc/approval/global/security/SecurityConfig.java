package com.umc.approval.global.security;

import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.security.filter.CustomAuthenticationFilter;
import com.umc.approval.global.security.filter.CustomAuthorizationFilter;
import com.umc.approval.global.security.filter.CustomKakaoAuthenticationFilter;
import com.umc.approval.global.security.service.KakaoOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final CustomAuthorizationFilter authorizationFilter;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final AuthenticationManagerBuilder authManagerBuilder;

    private final KakaoOAuth2Service kakaoOAuth2Service;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter authenticationFilter
                = new CustomAuthenticationFilter(authManagerBuilder.getOrBuild());
        // 로그인 인증 필터
        authenticationFilter.setFilterProcessesUrl("/auth/login");
        authenticationFilter.setAuthenticationSuccessHandler(successHandler);
        authenticationFilter.setAuthenticationFailureHandler(failureHandler);

        // 카카오 로그인 인증 필터
        CustomKakaoAuthenticationFilter customKakaoAuthenticationFilter
                = new CustomKakaoAuthenticationFilter(kakaoOAuth2Service, userRepository, authManagerBuilder.getOrBuild());
        customKakaoAuthenticationFilter.setFilterProcessesUrl("/auth/kakao");
        customKakaoAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
        customKakaoAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);

        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS) // Using JWT
                .and()
                .addFilter(customKakaoAuthenticationFilter)
                .addFilter(authenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider)
                .authorizeRequests()
                .anyRequest().permitAll();

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.httpFirewall(defaultHttpFirewall());
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

}
