package com.umc.approval.domain.user.service;

import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserDto.Request userCreateRequest){
        // 이메일 중복 체크
        userRepository.findByEmail(userCreateRequest.getEmail())
                .ifPresent(user -> {throw new CustomException(EMAIL_ALREADY_EXIST);});

        // 전화번호 중복 체크
        userRepository.findByPhoneNumber(userCreateRequest.getPhoneNumber())
                .ifPresent(user -> {throw new CustomException(PHONE_NUMBER_ALREADY_EXIST);});

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userCreateRequest.getPassword());

        // 사용자 등록
        // 일반회원가입 최초 가입자는 level 0, promotionPoint 0L로 초기화
        User user = User.builder()
                .nickname(userCreateRequest.getNickname())
                .email(userCreateRequest.getEmail())
                .password(encodedPassword)
                .phoneNumber(userCreateRequest.getPhoneNumber())
                .level(0)
                .promotionPoint(0L)
                .build();

        userRepository.save(user);
    }

    public void logout() {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        user.deleteRefreshToken();
    }
}
