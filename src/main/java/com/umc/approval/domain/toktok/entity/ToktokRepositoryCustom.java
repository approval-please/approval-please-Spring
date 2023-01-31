package com.umc.approval.domain.toktok.entity;

import com.umc.approval.domain.follow.entity.Follow;

import java.util.List;

public interface ToktokRepositoryCustom {

    List<Toktok> findAllByQuery(String query, Integer isTag, Integer category, Integer sortBy);

    List<Toktok> findAllByOption(Long userId, List<Follow> follows, Integer sortBy);
}
