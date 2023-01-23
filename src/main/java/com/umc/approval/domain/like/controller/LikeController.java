package com.umc.approval.domain.like.controller;

import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestBody LikeDto.ListRequest requestDto
    ) {
        return ResponseEntity.ok(likeService.getLikeList(request, pageable, requestDto));
    }
}
