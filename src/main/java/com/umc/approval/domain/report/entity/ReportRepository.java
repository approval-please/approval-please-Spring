package com.umc.approval.domain.report.entity;

import com.umc.approval.domain.document.entity.Document;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByDocumentId(Long documentId);

    @Query("select r from Report r " +
            "join fetch r.document d " +
            "join fetch d.user u " +
            "where r.id = :reportId")
    Optional<Report> findByIdWithUser(@Param("reportId") Long reportId);
}
