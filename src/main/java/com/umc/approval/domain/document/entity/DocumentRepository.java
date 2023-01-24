package com.umc.approval.domain.document.entity;

import com.umc.approval.global.type.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Modifying
    @Query(value = "update document set view = view + 1 where document_id = :document_id", nativeQuery = true)
    void updateView(@Param("document_id") Long document_id);

    @Modifying
    @Query(value = "update document set state = 0 where document_id = :document_id", nativeQuery = true)
    void updateStateApproved(@Param("document_id") Long document_id);

    @Modifying
    @Query(value = "update document set state = 1 where document_id = :document_id", nativeQuery = true)
    void updateStateRejected(@Param("document_id") Long document_id);

    @Query("select i from Document i where (i.state = 0 or i.state = 1) and i.user.id = :user_id")
    Page<Document> findByUserId(@Param("user_id") Long userId, Pageable pageable);
    
    @Query("select d from Document d " +
            "join fetch d.user u " +
            "where d.id = :documentId")
    Optional<Document> findByIdWithUser(Long documentId);

    // 마이페이지 - 사원증 조회
    @Query(value = "select d from Document d where d.user.id = :userId")
    List<Document> findAllByUserId(@Param("userId") Long userId); // 사용자가 작성한 결재서류 전체 조회

    @Query(value = "select d from Document d where d.user.id = :userId AND d.state = :state")
    List<Document> findAllByState(@Param("userId") Long userId, @Param("state") Integer state); // 사용자가 작성한 결재서류 상태별 조회

    @Query(value = "select d from Document d where d.id = (select a.document.id from Approval a where a.isApprove = :isApproved AND a.user.id = :userId)")
    List<Document> findAllByApproval(@Param("userId") Long userId, @Param("isApproved") Boolean isApproved); // 사용자가 결재한 결재서류 승인별 조회

    @Query(value = "select d from Document d where category = :category")
    Page<Document> findAllByCategory(@Param("category") CategoryType categoryType, Pageable pageable);
}