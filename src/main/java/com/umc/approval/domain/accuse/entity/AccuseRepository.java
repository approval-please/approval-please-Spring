package com.umc.approval.domain.accuse.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AccuseRepository extends JpaRepository<Accuse, Long>, AccuseRepositoryCustom {
    List<Accuse> findByReportId(Long reportId);
    List<Accuse> findByToktokId(Long toktokId);
    List<Accuse> findByDocumentId(Long documentId);
    @Query(value = "select * from accuse where comment_id in (:comment_id_list)", nativeQuery = true)
    List<Accuse> findByCommentId(@Param("comment_id_list") List<Long> commentIdList);

    @Transactional
    @Modifying
    @Query(value = "delete from accuse where comment_id in :comment_list", nativeQuery = true)
    void deleteByCommentIds(@Param("comment_list") List<Long> commentList);

    @Modifying
    @Query("delete from Accuse a where a.comment.id = :commentId")
    void deleteByCommentId(Long commentId);
}
