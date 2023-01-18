package com.umc.approval.domain.tag.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Modifying
    @Query(value = "delete from tag where document_id = :document_id", nativeQuery = true)
    void deleteByDocumentId(@Param("document_id") Long documentId);

}
