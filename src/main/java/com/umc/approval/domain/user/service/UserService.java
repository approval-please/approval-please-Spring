package com.umc.approval.domain.user.service;

import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomErrorType;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    // 임시 사용자(삭제 예정..)
    public Optional<User> getTemporaryUser(){
        return userRepository.findById(1L);
    }

    public void logout() {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        user.deleteRefreshToken();
    }
}
