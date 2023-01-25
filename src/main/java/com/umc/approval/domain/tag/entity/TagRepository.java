package com.umc.approval.domain.tag.entity;

import com.umc.approval.domain.toktok.entity.Toktok;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("select t from Tag t where t.toktok.id = :toktokId")
    List<Tag> findByToktokId(@Param("toktokId") Long toktokId);

    List<Tag> findByDocumentId(Long documentId);

    List<Tag> findByReportId(Long reportId);

    @Query(value = "select tag from tag where document_id = :document_id", nativeQuery = true)
    List<String> findTagNameList(@Param("document_id") Long documentId);

    @Query(value = "select tag from tag where report_id = :report_id", nativeQuery = true)
    List<String> findTagNameListByReportId(@Param("report_id") Long reportId);
}
