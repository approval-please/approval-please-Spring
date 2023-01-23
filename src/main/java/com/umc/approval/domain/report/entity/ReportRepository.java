package com.umc.approval.domain.report.entity;

import com.umc.approval.domain.document.entity.Document;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByDocumentId(Long documentId);
}
