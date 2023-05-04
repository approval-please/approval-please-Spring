package com.umc.approval.domain.link.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface LinkRepository extends JpaRepository<Link, Long> {
    @Query("select l from Link l where l.toktok.id = :toktokId")
    List<Link> findByToktokId(@Param("toktokId") Long toktokId);

    List<Link> findByReportId(Long reportId);

    @Query(value = "select link_url from link where report_id = :report_id", nativeQuery = true)
    List<String> findLinkUrlList(@Param("report_id") Long reportId);

    @Query("select l from Link l where l.document.id = :documentId")
    Optional<Link> findByDocumentId(@Param("documentId") Long documentId);
}

