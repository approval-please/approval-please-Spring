package com.umc.approval.domain.like.controller;

import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
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
            @RequestParam(value = "documentId", required = false) Long documentId,
            @RequestParam(value = "toktokId", required = false) Long toktokId,
            @RequestParam(value = "reportId", required = false) Long reportId
    ) {
        return ResponseEntity.ok(likeService.getLikeList(request, documentId, toktokId, reportId));
    }

    @PostMapping
    public ResponseEntity<LikeDto.UpdateResponse> like(@RequestBody LikeDto.Request requestDto) {
        return ResponseEntity.ok(likeService.like(requestDto));
    }
}
