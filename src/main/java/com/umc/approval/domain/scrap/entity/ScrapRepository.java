package com.umc.approval.domain.scrap.entity;

import com.umc.approval.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    Long countByReport(Report report);

}