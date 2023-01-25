package com.umc.approval.domain.toktok.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ToktokRepository extends JpaRepository<Toktok, Long> {

    @Query("select t from Toktok t " +
            "join fetch t.user u " +
            "where t.id = :toktokId")
    Optional<Toktok> findByIdWithUser(@Param("toktokId") Long toktokId);
}
