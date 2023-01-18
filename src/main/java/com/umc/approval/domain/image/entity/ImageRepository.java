package com.umc.approval.domain.image.entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query(value = "select image_url from image where document_id = :document_id", nativeQuery = true)
    List<String> findImageUrl(@Param("document_id") Long documentId);

    @Modifying
    @Query(value = "delete from image where document_id = :document_id", nativeQuery = true)
    void deleteByDocumentId(@Param("document_id") Long documentId);

}
