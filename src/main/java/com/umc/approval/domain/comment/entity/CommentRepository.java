package com.umc.approval.domain.comment.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "where c.id = :commentId")
    Optional<Comment> findByIdWithUser(Long commentId);

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "left join fetch c.parentComment pc " +
            "where c.id = :commentId")
    Optional<Comment> findByIdWithUserAndParentComment(Long commentId);

    Boolean existsByParentCommentId(Long parentCommentId);
}
