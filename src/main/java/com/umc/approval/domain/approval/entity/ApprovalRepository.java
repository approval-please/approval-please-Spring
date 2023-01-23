package com.umc.approval.domain.approval.entity;

import com.umc.approval.domain.approval.controller.ApprovalController;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    @Query(value = "select count(*) from approval where user_id = :user_id and document_id = :document_id", nativeQuery = true)
    int countByUserIdAndDocumentId(@Param("user_id") Long userId, @Param("document_id") Long documentId);

    @Query(value = "select count(*) from approval where document_id = :document_id and is_approve = 1", nativeQuery = true)
    int countApproveByDocumentId(@Param("document_id") Long documentId);

    @Query(value = "select count(*) from approval where document_id = :document_id and is_approve = 0", nativeQuery = true)
    int countRejectByDocumentId(@Param("document_id") Long documentId);

}
