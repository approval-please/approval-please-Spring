package com.umc.approval.domain.comment.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "where c.id = :commentId")
    Optional<Comment> findByIdWithUser(@Param("commentId") Long commentId);

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "left join fetch c.parentComment pc " +
            "where c.id = :commentId")
    Optional<Comment> findByIdWithUserAndParentComment(@Param("commentId") Long commentId);

    Boolean existsByParentCommentId(Long parentCommentId);

    @Query(value = "select count(*) from comment where document_id = :document_id", nativeQuery = true)
    int countByDocumentId(@Param("document_id") Long documentId);
}
