package com.umc.approval.domain.like.entity;

import com.umc.approval.domain.like.dto.LikeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LikeRepositoryCustom {
    Page<Like> findAllByPost(Pageable pageable, LikeDto.Request requestDto);
    Optional<Like> findByUserAndPost(Long userId, LikeDto.Request requestDto);
}
