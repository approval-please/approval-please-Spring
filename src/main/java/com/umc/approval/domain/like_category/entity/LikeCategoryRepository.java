package com.umc.approval.domain.like_category.entity;

import com.umc.approval.global.type.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeCategoryRepository extends JpaRepository<LikeCategory, Long> {

    @Query("select l from LikeCategory l " +
            "where l.user.id = :userId")
    List<LikeCategory> findByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from LikeCategory l " +
            "where l.category = :categoryType " +
            "and l.user.id = :userId")
    void deleteByCategoryAndUserId(@Param("categoryType") CategoryType categoryType, @Param("userId") Long userId);
}
