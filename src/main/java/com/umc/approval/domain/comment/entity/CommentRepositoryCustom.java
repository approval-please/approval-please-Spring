package com.umc.approval.domain.comment.entity;

import com.umc.approval.domain.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> findAllByPost(CommentDto.Request requestDto);

    Page<Comment> findAllByPostPaging(Pageable pageable, CommentDto.Request requestDto);

    Integer countByPost(CommentDto.Request requestDto);
}
