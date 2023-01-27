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

    @Modifying
    @Query(value = "update users set promotion_point = promotion_point + :point where user_id = :user_id", nativeQuery = true)
    void updatePoint(@Param("user_id") Long userId, @Param("point") Long point);

    @Modifying
    @Query(value = "update users set promotion_point = promotion_point + :point where user_id in (:user_id_list)", nativeQuery = true)
    void updatePoint(@Param("user_id_list") List<Long> userIdList, @Param("point")Long point);
}
