package com.umc.approval.domain.comment.controller;

import com.umc.approval.domain.comment.dto.CommentDto;
import com.umc.approval.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/comments")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;


    @GetMapping
    public ResponseEntity<Slice<CommentDto.ParentResponse>> getCommentList(
            HttpServletRequest request,
            @RequestParam(value = "documentId", required = false) Long documentId,
            @RequestParam(value = "toktokId", required = false) Long toktokId,
            @RequestParam(value = "reportId", required = false) Long reportId,
            @RequestParam(value = "lastCommentId", required = false) Long lastCommentId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(
                commentService.getCommentList(request, documentId, toktokId, reportId, lastCommentId, pageable)
        );
    }

    @PostMapping
    public ResponseEntity<Void> createComment(@Valid @RequestBody CommentDto.CreateRequest requestDto) {
        commentService.createComment(requestDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDto.UpdateRequest requestDto
    ) {
        commentService.updateComment(commentId, requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
