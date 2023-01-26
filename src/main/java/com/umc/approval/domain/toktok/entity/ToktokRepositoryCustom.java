package com.umc.approval.domain.toktok.entity;

import java.util.List;

public interface ToktokRepositoryCustom {

    List<Toktok> findAllByQuery(String query, Integer isTag, Integer category, Integer sortBy);
}
