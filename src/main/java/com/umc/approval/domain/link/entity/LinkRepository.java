package com.umc.approval.domain.link.entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LinkRepository extends JpaRepository<Link, Long> {
    @Modifying
    @Query(value = "delete from link where document_id =: document_id", nativeQuery = true)
    void deleteByDocumentId(@Param("document_id") Long documentId);
}