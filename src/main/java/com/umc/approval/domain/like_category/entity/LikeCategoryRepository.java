package com.umc.approval.domain.like_category.entity;
import com.umc.approval.global.type.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeCategoryRepository extends JpaRepository<LikeCategory, Long> {

    @Query(value = "select category from like_category where user_id = :user_id", nativeQuery = true)
    List<CategoryType> findCategoryListByUserId(@Param("user_id") Long userId);
}
