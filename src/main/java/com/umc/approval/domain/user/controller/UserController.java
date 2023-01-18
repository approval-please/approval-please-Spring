package com.umc.approval.domain.user.controller;

import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/signup")
    public ResponseEntity<Void> signup(
            @RequestBody final UserDto.Request userCreateRequest
    ) {
        userService.signup(userCreateRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        userService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("auth/refresh")
    public ResponseEntity<UserDto.TokenResponse> refresh(HttpServletRequest request) {
        return ResponseEntity.ok(userService.refresh(request));
    }

    @PostMapping("/auth/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody UserDto.ResetPasswordRequest requestDto) {
        userService.resetPassword(requestDto);
        return ResponseEntity.ok().build();
    }
}
