package com.umc.approval.domain.user.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findBySocialId(Long socialId);

    List<User> findByNicknameContains(String query);

    @Query("select u from Approval a " +
            "join fetch a.user u " +
            "where a.document.id = :documentId " +
            "and a.isApprove = :isApprove")
    List<User> findByApprove(@Param("documentId") Long documentId, @Param("isApprove") Boolean isApprove);
}
