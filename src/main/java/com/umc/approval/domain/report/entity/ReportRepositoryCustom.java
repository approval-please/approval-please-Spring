package com.umc.approval.domain.report.entity;

import com.umc.approval.domain.follow.entity.Follow;

import java.util.List;

public interface ReportRepositoryCustom {

    List<Report> findAllByQuery(String query, Integer isTag, Integer category, Integer sortBy);

    List<Report> findAllByOption(Long userId, List<Follow> follows, Integer sortBy);
}