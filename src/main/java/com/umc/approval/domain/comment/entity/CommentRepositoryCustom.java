package com.umc.approval.domain.comment.entity;

import com.umc.approval.domain.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    Page<Comment> findAllByPost(Pageable pageable, CommentDto.Request requestDto);

    Integer countByPost(CommentDto.Request requestDto);
}
