package com.umc.approval.domain.user.service;

import com.umc.approval.domain.user.dto.UserCreateRequest;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    //TODO: 전화번호 인증 및 중복체크 관련 예외처리
    public User signup(UserCreateRequest userCreateRequest){
        User user = userCreateRequest.toEntity();
        return userRepository.save(user);
    }

    public void logout() {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        user.deleteRefreshToken();
    }
}
