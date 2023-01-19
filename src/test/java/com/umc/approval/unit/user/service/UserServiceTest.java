package com.umc.approval.unit.user.service;

import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.domain.user.service.UserService;
import com.umc.approval.global.aws.service.AwsS3Service;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.umc.approval.global.exception.CustomErrorType.INVALID_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtService jwtService;

    @Mock
    AwsS3Service awsS3Service;

    @Mock
    PasswordEncoder passwordEncoder;

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .email("test" + id + "@test.com")
                .password("test123!" + id)
                .nickname("test" + id)
                .build();
    }

    @DisplayName("refresh token을 통해 access token 재발급에 성공한다")
    @Test
    void refresh_access_success() {

        // given
        User user = createUser(1L);
        String testToken = "test1234";
        String responseAccessToken = "responseAccessToken";
        user.updateRefreshToken(testToken);
        given(jwtService.getEmail(any())).willReturn(user.getEmail());
        given(jwtService.getToken(any())).willReturn(testToken);
        given(jwtService.calculateRefreshExpiredDays(any())).willReturn(200L);
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(jwtService.createAccessToken(any(), any())).willReturn(responseAccessToken);

        // when
        UserDto.NormalTokenResponse response = userService.refresh(new MockHttpServletRequest());

        // then
        assertThat(response.getAccessToken()).isEqualTo(responseAccessToken);
        assertThat(response.getRefreshToken()).isNull();
    }

    @DisplayName("refresh시 refresh token 만료기간이 30일 미만이면 refresh token도 재발급한다")
    @Test
    void refresh_access_refresh_success() {

        // given
        User user = createUser(1L);
        String testToken = "test1234";
        String responseAccessToken = "responseAccessToken";
        String responseRefreshToken = "responseRefreshToken";
        user.updateRefreshToken(testToken);
        given(jwtService.getEmail(any())).willReturn(user.getEmail());
        given(jwtService.getToken(any())).willReturn(testToken);
        given(jwtService.calculateRefreshExpiredDays(any())).willReturn(15L);
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(jwtService.createAccessToken(any(), any())).willReturn(responseAccessToken);
        given(jwtService.createRefreshToken(any())).willReturn(responseRefreshToken);

        // when
        UserDto.NormalTokenResponse response = userService.refresh(new MockHttpServletRequest());

        // then
        assertThat(response.getAccessToken()).isEqualTo(responseAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(responseRefreshToken);
    }

    @DisplayName("token 재발급 시 db 내 refresh token과 일치하지 않으면 실패한다")
    @Test
    void refresh_db_not_matched_fail() {
        // given
        User user = createUser(1L);
        String testToken = "test1234";
        user.updateRefreshToken(testToken);
        given(jwtService.getEmail(any())).willReturn(user.getEmail());
        given(jwtService.getToken(any())).willReturn(testToken + "dummy");

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> userService.refresh(new MockHttpServletRequest()));
        assertThat(e.getErrorType()).isEqualTo(INVALID_TOKEN);
    }
}
