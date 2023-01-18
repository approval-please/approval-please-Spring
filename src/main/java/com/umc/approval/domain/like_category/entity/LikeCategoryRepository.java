package com.umc.approval.domain.like_category.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeCategoryRepository extends JpaRepository<LikeCategory, Long> {
}
