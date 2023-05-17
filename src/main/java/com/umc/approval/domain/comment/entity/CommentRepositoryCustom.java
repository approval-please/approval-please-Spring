package com.umc.approval.domain.comment.entity;

import com.umc.approval.global.util.BooleanBuilderUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> findAllByPost(BooleanBuilderUtil.PostIds postIds);

    Slice<Comment> findAllByPostSlice(Pageable pageable, BooleanBuilderUtil.PostIds postIds);

    Integer countByPost(BooleanBuilderUtil.PostIds postIds);

    boolean existsParentCommentByPost(Long parentCommentId, BooleanBuilderUtil.PostIds postIds);
}
