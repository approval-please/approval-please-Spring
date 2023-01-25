package com.umc.approval.domain.report.entity;

import com.umc.approval.domain.document.entity.Document;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByDocumentId(Long documentId);

    @Query("select r from Report r " +
            "join fetch r.document d " +
            "join fetch d.user u " +
            "where r.id = :reportId")

    @Modifying
    @Query(value = "update report set view = view + 1 where report_id = :report_id", nativeQuery = true)
    void updateView(@Param("report_id") Long report_id);

    Optional<Report> findByIdWithUser(@Param("reportId") Long reportId);
}
