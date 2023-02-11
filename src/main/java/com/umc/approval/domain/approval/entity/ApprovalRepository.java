package com.umc.approval.domain.approval.entity;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    @Query(value = "select count(*) from approval where user_id = :user_id and document_id = :document_id", nativeQuery = true)
    int countByUserIdAndDocumentId(@Param("user_id") Long userId, @Param("document_id") Long documentId);

    @Query(value = "select count(*) from approval where document_id = :document_id and is_approve = 1", nativeQuery = true)
    int countApproveByDocumentId(@Param("document_id") Long documentId);

    @Query(value = "select count(*) from approval where document_id = :document_id and is_approve = 0", nativeQuery = true)
    int countRejectByDocumentId(@Param("document_id") Long documentId);

    @Query(value = "select * from approval where document_id = :document_id", nativeQuery = true)
    List<Approval> findByDocumentId(@Param("document_id") Long documentId);

    Optional<Approval> findByUserAndDocument(User user, Document document);

    @Query("select a from Approval a " +
            "join fetch a.user u " +
            "where a.document.id = :documentId " +
            "and a.isApprove = :isApprove")
    List<Approval> findAllUserByDocumentIsApprove(@Param("documentId") Long documentId, @Param("isApprove") Boolean isApprove);

    @Query("select a from Approval a " +
            "join fetch a.user u " +
            "where a.document.id = :documentId")
    List<Approval> findAllUserByApproval(@Param("documentId") Long documentId);
}
