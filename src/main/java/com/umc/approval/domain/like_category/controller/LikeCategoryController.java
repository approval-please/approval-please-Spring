package com.umc.approval.domain.like_category.controller;

import com.umc.approval.domain.like_category.dto.LikeCategoryDto;
import com.umc.approval.domain.like_category.service.LikeCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/documents/likedCategory")
@RequiredArgsConstructor
@RestController
public class LikeCategoryController {
    private final LikeCategoryService likeCategoryService;

    @GetMapping("/my")
    public ResponseEntity<LikeCategoryDto.Response> getLikeCategoryList() {
        return ResponseEntity.ok(likeCategoryService.getLikeCategoryList());
    }

    @PostMapping
    public ResponseEntity<Void> likeCategory(
            @RequestBody LikeCategoryDto.Request likeCategoryRequest
    ) {
        likeCategoryService.likeCategory(likeCategoryRequest);
        return ResponseEntity.ok().build();
    }
}
