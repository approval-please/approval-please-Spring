package com.umc.approval.domain.like.controller;

import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/likes")
@RequiredArgsConstructor
@RestController
public class LikeController {

    private final LikeService likeService;

    @GetMapping
    public ResponseEntity<LikeDto.ListResponse> getLikeList(@RequestBody LikeDto.ListRequest requestDto) {
        return ResponseEntity.ok(likeService.getLikeList(requestDto));
    }
}
