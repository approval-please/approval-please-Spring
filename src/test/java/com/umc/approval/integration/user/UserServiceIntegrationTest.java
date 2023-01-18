package com.umc.approval.integration.user;

import com.umc.approval.config.AwsS3MockConfig;
import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.domain.user.service.UserService;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(AwsS3MockConfig.class)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EntityManager em;

    private User createUser(Long id) {
        return User.builder()
                .email("test" + id + "@test.com")
                .password("test123!")
                .nickname("test" + id)
                .phoneNumber("010-1234-5678")
                .level(0)
                .promotionPoint(0L)
                .build();
    }

    @DisplayName("logout에 성공한다")
    @Test
    void logout_success() {

        // given
        userRepository.save(createUser(1L));
        User user = userRepository.findAll().get(0);
        String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtService.createRefreshToken(user.getEmail());
        user.updateRefreshToken(refreshToken);
        em.flush();
        em.clear();

        // SecurityContextHolder에 accessToken 포함하여 저장
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when
        userService.logout();

        // then
        User findUser = userRepository.findByEmail(user.getEmail()).get();
        assertThat(findUser.getRefreshToken()).isNull();
    }

    @DisplayName("logout - 사용자가 존재하지 않으면 실패한다")
    @Test
    void logout_user_not_found_fail() {

        // given
        userRepository.save(createUser(1L));
        User user = userRepository.findAll().get(0);
        // 다른 user의 access token
        String accessToken = jwtService.createAccessToken(user.getEmail() + "dum", user.getId() + 1L);
        String refreshToken = jwtService.createRefreshToken(user.getEmail());
        user.updateRefreshToken(refreshToken);
        em.flush();
        em.clear();

        // SecurityContextHolder에 accessToken 포함하여 저장
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> userService.logout());
        assertThat(e.getErrorType()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("logout - access token이 존재하지 않으면 실패한다")
    @Test
    void logout_token_not_found_fail() {
        // given & when & then
        assertThrows(NullPointerException.class, () -> userService.logout());
    }

    @DisplayName("비밀번호 재설정에 성공한다")
    @Test
    void reset_password_success() {

        // given
        User user = createUser(1L);
        userRepository.save(user);
        em.flush();
        em.clear();
        String newPassword = "testNew12345!";
        UserDto.ResetPasswordRequest requestDto = new UserDto.ResetPasswordRequest(user.getEmail(), newPassword);

        // when
        userService.resetPassword(requestDto);
        User findUser = userRepository.findAll().get(0);

        // then
        assertThat(passwordEncoder.matches(newPassword, findUser.getPassword())).isTrue();
    }

    @DisplayName("비밀번호 재설정 시 사용자가 없으면 실패한다")
    @Test
    void reset_password_user_not_found_fail() {

        // given
        User user = createUser(1L);
        userRepository.save(user);
        em.flush();
        em.clear();
        String newPassword = "testNew12345!";
        // 존재하지 않는 사용자 이메일 입력
        UserDto.ResetPasswordRequest requestDto = new UserDto.ResetPasswordRequest("dummy@test.com", newPassword);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> userService.resetPassword(requestDto));
        assertThat(e.getErrorType()).isEqualTo(USER_NOT_FOUND);
    }
}
