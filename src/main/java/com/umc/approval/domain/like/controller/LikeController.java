package com.umc.approval.domain.like.controller;

import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/likes")
@RequiredArgsConstructor
@RestController
public class LikeController {

    private final LikeService likeService;

    @GetMapping
    public ResponseEntity<LikeDto.ListResponse> getLikeList(
            HttpServletRequest request,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestBody LikeDto.Request requestDto
    ) {
        return ResponseEntity.ok(likeService.getLikeList(request, pageable, requestDto));
    }

    @PostMapping
    public ResponseEntity<LikeDto.UpdateResponse> like(@RequestBody LikeDto.Request requestDto) {
        return ResponseEntity.ok(likeService.like(requestDto));
    }
}
