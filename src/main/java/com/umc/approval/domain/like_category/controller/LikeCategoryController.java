package com.umc.approval.domain.like_category.controller;

import com.umc.approval.domain.like_category.dto.LikeCategoryDto;
import com.umc.approval.domain.like_category.service.LikeCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/documents/likedCategory")
@RequiredArgsConstructor
@RestController
public class LikeCategoryController {
    private final LikeCategoryService likeCategoryService;

    @GetMapping("/my")
    public ResponseEntity<LikeCategoryDto.Response> getLikeCategoryList(
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(likeCategoryService.getLikeCategoryList(request));
    }

    @PostMapping
    public ResponseEntity<Void> likeCategory(
            @RequestBody LikeCategoryDto.Request likeCategoryRequest
    ) {
        likeCategoryService.likeCategory(likeCategoryRequest);
        return ResponseEntity.ok().build();
    }
}
