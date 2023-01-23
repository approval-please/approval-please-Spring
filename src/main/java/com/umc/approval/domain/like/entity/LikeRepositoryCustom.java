package com.umc.approval.domain.like.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeRepositoryCustom {

    Page<Like> findAllByDocumentId(Pageable pageable, Long documentId);

    Page<Like> findAllByToktokId(Pageable pageable, Long toktokId);

    Page<Like> findAllByReportId(Pageable pageable, Long reportId);
}
