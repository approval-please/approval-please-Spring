package com.umc.approval.unit.user.service;

import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.domain.user.service.UserService;
import com.umc.approval.global.aws.service.AwsS3Service;
import com.umc.approval.global.security.service.JwtService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
