package com.umc.approval.domain.link.entity;
import com.umc.approval.domain.toktok.entity.Toktok;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface LinkRepository extends JpaRepository<Link, Long> {
    @Query("select l from Link l where l.toktok.id = :toktokId")
    List<Link> findByToktokId(@Param("toktokId") Long toktokId);

    List<Link> findByReportId(Long reportId);

    @Query("select l from Link l where l.document.id = :documentId")
    Optional<Link> findByDocumentId(Long documentId);
}

