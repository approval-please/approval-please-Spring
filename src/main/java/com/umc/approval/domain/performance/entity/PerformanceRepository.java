package com.umc.approval.domain.performance.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    @Modifying
    @Query(value = "select p from Performance p where p.user.id = :user_id")
    List<Performance> findByUserId(@Param("user_id") Long user_id);
}
