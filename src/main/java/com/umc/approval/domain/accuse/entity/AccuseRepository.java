package com.umc.approval.domain.accuse.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccuseRepository extends JpaRepository<Accuse, Long>, AccuseRepositoryCustom {
    List<Accuse> findByReportId(Long reportId);
    List<Accuse> findByToktokId(Long toktokId);
    List<Accuse> findByDocumentId(Long documentId);
    @Query(value = "select * from accuse where comment_id in (:comment_id_list)", nativeQuery = true)
    List<Accuse> findByCommentId(@Param("comment_id_list") List<Long> commentIdList);
}
