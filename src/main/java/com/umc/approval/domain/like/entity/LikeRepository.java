package com.umc.approval.domain.like.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {

    @Query(value = "select count(*) from likes where document_id = :document_id", nativeQuery = true)
    int countByDocumentId(@Param("document_id") Long documentId);

    @Query("select l from Like l " +
            "join fetch l.comment c " +
            "where l.user.id = :userId " +
            "and l.comment.id in :commentIds")
    List<Like> findAllByUserAndCommentIn(Long userId, List<Long> commentIds);
}
