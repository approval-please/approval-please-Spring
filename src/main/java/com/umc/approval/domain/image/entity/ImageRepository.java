package com.umc.approval.domain.image.entity;
import com.umc.approval.domain.toktok.entity.Toktok;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("select i from Image i where i.toktok.id = :toktokId")
    List<Image> findByToktokId(@Param("toktokId") Long toktokId);

    @Modifying
    @Query(value = "delete from image where toktok_id = :toktok_id", nativeQuery = true)
    void deleteByToktokId(@Param("toktok_id") Long toktokId);

    @Modifying
    @Query(value = "delete from image where document_id = :document_id", nativeQuery = true)
    void deleteByDocumentId(@Param("document_id") Long documentId);

    List<Image> findByDocumentId(Long documentId);

    @Query(value = "select image_url from image where document_id = :document_id", nativeQuery = true)
    List<String> findImageUrlList(@Param("document_id") Long documentId);

    List<Image> findByReportId(Long reportId);
}
