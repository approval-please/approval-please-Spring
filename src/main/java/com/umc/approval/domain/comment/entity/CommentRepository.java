package com.umc.approval.domain.comment.entity;

import com.umc.approval.domain.report.entity.Report;
import java.util.List;
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

    List<Comment> findByReportId(Long reportId);

    List<Comment> findByToktokId(Long toktokId);

    List<Comment> findByDocumentId(Long documentId);

}
