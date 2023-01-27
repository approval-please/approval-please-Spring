package com.umc.approval.domain.scrap.entity;

import com.umc.approval.domain.scrap.dto.ScrapDto;

import java.util.Optional;

public interface ScrapRepositoryCustom {
    Optional<Scrap> findByUserAndPost(Long userId, ScrapDto.Request requestDto);
}
