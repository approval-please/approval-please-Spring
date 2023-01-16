package com.umc.approval.global.security;

import com.umc.approval.global.security.filter.CustomAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    //    private final CustomAuthorizationFilter authorizationFilter;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final AuthenticationManagerBuilder authManagerBuilder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter authenticationFilter
                = new CustomAuthenticationFilter(authManagerBuilder.getOrBuild());
        // 로그인 인증 필터
        authenticationFilter.setFilterProcessesUrl("/auth/login");
        authenticationFilter.setAuthenticationSuccessHandler(successHandler);
        authenticationFilter.setAuthenticationFailureHandler(failureHandler);

        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS) // Using JWT
                .and()
                .addFilter(authenticationFilter)
//                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider)
                .authorizeRequests()
                .anyRequest().permitAll();

        return http.build();
    }
}
