package com.umc.approval.domain.report.entity;

import java.util.List;

public interface ReportRepositoryCustom {

    List<Report> findAllByQuery(String query, Integer isTag, Integer category, Integer sortBy);
}