package com.umc.approval.domain.document.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Modifying
    @Query(value = "update document set view = view + 1 where document_id = :document_id", nativeQuery = true)
    void updateView(@Param("document_id") Long document_id);

}