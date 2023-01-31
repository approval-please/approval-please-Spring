package com.umc.approval.domain.document.entity;

import com.umc.approval.global.type.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocumentRepositoryCustom {

    List<Document> findAllByQuery(String query, Integer isTag, Integer category, Integer state, Integer sortBy);

    Page<Document> findAllByQueryPaging(String query, Integer isTag, Integer category, Integer state, Integer sortBy, Pageable pageable);

    List<Document> findAllByOption(Integer category, Integer state, Integer sortBy);

    List<Document> findAllByLikedCategories(List<CategoryType> categories, Integer state, Integer sortBy);
}
