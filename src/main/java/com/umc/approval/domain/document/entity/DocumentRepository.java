package com.umc.approval.domain.document.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Modifying
    @Query(value = "update document set view = view + 1 where document_id = :document_id", nativeQuery = true)
    void updateView(@Param("document_id") Long document_id);

    @Query("select i from Document i where (i.state = 0 or i.state = 1) and i.user.id = :user_id")
    Page<Document> findByUserId(@Param("user_id") Long userId, Pageable pageable);

    @Query("select d from Document d " +
            "join fetch d.user u " +
            "where d.id = :documentId")
    Optional<Document> findByIdWithUser(Long documentId);
}