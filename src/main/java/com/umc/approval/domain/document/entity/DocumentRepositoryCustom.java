package com.umc.approval.domain.document.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentRepositoryCustom {

    Page<Document> findAllByQuery(String query, Integer isTag, Integer category, Integer state, Integer sortBy, Pageable pageable);
}
