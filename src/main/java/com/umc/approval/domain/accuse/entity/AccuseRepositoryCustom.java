package com.umc.approval.domain.accuse.entity;

import com.umc.approval.domain.accuse.dto.AccuseDto;

import java.util.Optional;

public interface AccuseRepositoryCustom {
    Optional<Accuse> findByUserAndPost(Long userId, AccuseDto.Request accuseRequest);
}
