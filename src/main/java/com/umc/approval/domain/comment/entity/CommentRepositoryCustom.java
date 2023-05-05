package com.umc.approval.domain.comment.entity;

import com.umc.approval.global.util.BooleanBuilderUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> findAllByPost(BooleanBuilderUtil.PostIds postIds);

    Page<Comment> findAllByPostPaging(Pageable pageable, BooleanBuilderUtil.PostIds postIds);

    Integer countByPost(BooleanBuilderUtil.PostIds postIds);

    boolean existsParentCommentByPost(Long parentCommentId, BooleanBuilderUtil.PostIds postIds);
}
