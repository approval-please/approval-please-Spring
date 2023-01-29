package com.umc.approval.domain.user.controller;

import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/email")
    public ResponseEntity<UserDto.EmailCheckResponse> emailCheck(@RequestBody UserDto.EmailCheckRequest requestDto) {
        return ResponseEntity.ok(userService.emailCheck(requestDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(
            @RequestBody final UserDto.NormalRequest userCreateNormalRequest
    ) {
        userService.signup(userCreateNormalRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup/sns")
    public ResponseEntity<Void> snsSignup(@RequestBody UserDto.SnsRequest requestDto) {
        userService.snsSignup(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        userService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserDto.NormalTokenResponse> refresh(HttpServletRequest request) {
        return ResponseEntity.ok(userService.refresh(request));
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody UserDto.ResetPasswordRequest requestDto) {
        userService.resetPassword(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/token/check")
    public ResponseEntity<Void> checkToken() {
        userService.checkToken();
        return ResponseEntity.ok().build();
    }
}
