package com.umc.approval.domain.comment.controller;

import com.umc.approval.domain.comment.dto.CommentDto;
import com.umc.approval.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/comments")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(
            @Valid @RequestPart(value = "data") CommentDto.CreateRequest requestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        commentService.createComment(requestDto, images);
        return ResponseEntity.ok().build();
    }
}
