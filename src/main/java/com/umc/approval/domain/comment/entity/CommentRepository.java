package com.umc.approval.domain.comment.entity;

import com.umc.approval.domain.report.entity.Report;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom, QuerydslPredicateExecutor<Comment> {

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

    @Query(value = "select count(*) from comment where document_id = :document_id and is_deleted = 0", nativeQuery = true)
    int countByDocumentId(@Param("document_id") Long documentId);

    @Query(value = "select count(*) from comment where report_id = :report_id and comment.is_deleted = 0", nativeQuery = true)
    Long countByReportId(@Param("report_id") Long reportId);

    @Query(value = "select count(*) from comment where toktok_id = :toktok_id and comment.is_deleted = 0", nativeQuery = true)
    Long countByToktokId(@Param("toktok_id") Long toktokId);

    List<Comment> findByReportId(Long reportId);

    List<Comment> findByToktokId(Long toktokId);

    List<Comment> findByDocumentId(Long documentId);

    @Query("select c from Comment c join fetch c.document d where c.user.id = :userId order by c.createdAt desc")
    List<Comment> findCommentByUserAndDocument(Long userId);

    @Query("select c from Comment c join fetch c.document d where c.user.id = :userId and d.state = :state order by c.createdAt desc")
    List<Comment> findCommentByUserAndDocumentAndState(Long userId, Integer state);

    @Query("select c from Comment c join fetch c.toktok t where c.user.id = :userId order by c.createdAt desc")
    List<Comment> findCommentsByUserAndToktok(Long userId);

    @Query("select c from Comment c join fetch c.report r where c.user.id = :userId order by c.createdAt desc")
    List<Comment> findCommentsByUserAndReport(Long userId);
}