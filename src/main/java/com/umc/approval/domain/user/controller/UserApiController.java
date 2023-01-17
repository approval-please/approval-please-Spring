package com.umc.approval.domain.user.controller;

import com.umc.approval.domain.user.dto.TokenResponseDto;
import com.umc.approval.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        userService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenResponseDto> refresh(HttpServletRequest request) {
        return ResponseEntity.ok(userService.refresh(request));
    }
}
