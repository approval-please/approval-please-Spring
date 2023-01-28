package com.umc.approval.domain.toktok.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ToktokRepository extends JpaRepository<Toktok, Long>, ToktokRepositoryCustom {

    @Query("select t from Toktok t " +
            "join fetch t.user u " +
            "where t.id = :toktokId")
    Optional<Toktok> findByIdWithUser(@Param("toktokId") Long toktokId);

    @Modifying
    @Query(value = "update toktok set view = view + 1 where toktok_id = :toktok_id", nativeQuery = true)
    void updateView(@Param("toktok_id") Long toktokId);

}
