package com.umc.approval.domain.approval.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    @Query(value = "select count(*) from approval where document_id = :document_id and is_approve = true", nativeQuery = true)
    int countApprovalByDocumentId(@Param("document_id") Long documentId);

    @Query(value = "select count(*) from approval where document_id = :document_id and is_approve = false", nativeQuery = true)
    int countRejectByDocumentId(@Param("document_id") Long documentId);

}
