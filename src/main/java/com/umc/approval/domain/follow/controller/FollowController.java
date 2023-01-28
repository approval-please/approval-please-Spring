package com.umc.approval.domain.follow.controller;

import com.umc.approval.domain.follow.dto.FollowDto;
import com.umc.approval.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/follow")
@RequiredArgsConstructor
@RestController
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<FollowDto.UpdateResponse> follow(
            @RequestBody FollowDto.Request followRequest
    ) {
        return ResponseEntity.ok(followService.follow(followRequest));
    }
}