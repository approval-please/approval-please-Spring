package com.umc.approval.domain.comment.entity;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.report.entity.Report;
import java.util.List;

import com.umc.approval.domain.toktok.entity.Toktok;
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

    @Query(value = "select count(*) from comment where report_id = :report_id and comment.is_deleted = 0", nativeQuery = true)
    Long countByReportId(@Param("report_id") Long reportId);

    @Query(value = "select count(*) from comment where toktok_id = :toktok_id and comment.is_deleted = 0", nativeQuery = true)
    Long countByToktokId(@Param("toktok_id") Long toktokId);

    List<Comment> findByReportId(Long reportId);

    List<Comment> findByToktokId(Long toktokId);

    List<Comment> findByDocumentId(Long documentId);

    @Query("select d from Document d where d.id = (select c.document.id from Comment c where c.user.id = :userId)")
    List<Document> findDocuments(Long userId);

    @Query("select d from Document d where d.id IN (select c.document.id from Comment c where c.user.id = :userId) AND d.state = :state")
    List<Document> findDocumentsByState(Long userId, Integer state);

    @Query("select t from Toktok t where t.id IN (select c.toktok.id from Comment c where c.user.id = :userId)")
    List<Toktok> findToktoks(Long userId);

    @Query("select r from Report r where r.id IN (select c.report.id from Comment c where c.user.id = :userId)")
    List<Report> findReports(Long userId);
}
