package com.umc.approval.domain.like.entity;

import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.global.util.BooleanBuilderUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LikeRepositoryCustom {

    List<Like> findAllByPost(BooleanBuilderUtil.PostIds postIds);

    Page<Like> findAllByPostPaging(Pageable pageable, BooleanBuilderUtil.PostIds postIds);
    Optional<Like> findByUserAndPost(Long userId, LikeDto.Request requestDto);
}
