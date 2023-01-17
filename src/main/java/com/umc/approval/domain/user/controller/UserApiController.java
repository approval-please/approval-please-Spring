package com.umc.approval.domain.user.controller;

import com.umc.approval.domain.user.dto.UserCreateRequest;
import com.umc.approval.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("/auth/signup")
    public ResponseEntity<Void> signup(
            @RequestBody final UserCreateRequest userCreateRequest
    ) {
        userService.signup(userCreateRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        userService.logout();
        return ResponseEntity.ok().build();
    }
}
