package com.umc.approval.domain.comment.entity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.approval.global.util.BooleanBuilderUtil;
import com.umc.approval.global.util.SliceUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import java.util.List;

import static com.umc.approval.domain.comment.entity.QComment.comment;


public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public CommentRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Comment> findAllByPost(BooleanBuilderUtil.PostIds postIds) {
        return queryFactory
                .selectFrom(comment)
                .innerJoin(comment.user).fetchJoin() // 댓글 유저 함께 조회
                .where(
                        BooleanBuilderUtil.postEq(comment.document, comment.toktok, comment.report, postIds),
                        comment.parentComment.id.isNull() // 대댓글이 아닌 것만
                )
                .orderBy(comment.id.desc())
                .fetch();
    }

    @Override
    public Slice<Comment> findAllByPostSlice(Pageable pageable, BooleanBuilderUtil.PostIds postIds, Long lastCommentId) {
        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .innerJoin(comment.user).fetchJoin() // 댓글 유저 함께 조회
                .where(
                        ltCommentId(lastCommentId),
                        BooleanBuilderUtil.postEq(comment.document, comment.toktok, comment.report, postIds),
                        comment.parentComment.id.isNull() // 대댓글이 아닌 것만
                )
                .orderBy(comment.id.desc())
                .limit(pageable.getPageSize() + 1) // 다음 페이지 존재 여부를 위해 pageSize + 1 조회
                .fetch();

        return SliceUtil.slice(comments, pageable);
    }

    private BooleanExpression ltCommentId(Long commentId) {
        return commentId == null ? null : comment.id.lt(commentId);
    }

    @Override
    public Integer countByPost(BooleanBuilderUtil.PostIds postIds) {
        return Math.toIntExact(queryFactory
                .select(comment.count())
                .where(BooleanBuilderUtil.postEq(comment.document, comment.toktok, comment.report, postIds))
                .from(comment)
                .fetchFirst());
    }

    @Override
    public boolean existsParentCommentByPost(Long parentCommentId, BooleanBuilderUtil.PostIds postIds) {
        return queryFactory
                .selectOne()
                .from(comment)
                .where(
                        BooleanBuilderUtil.postEq(comment.document, comment.toktok, comment.report, postIds),
                        comment.parentComment.id.eq(parentCommentId)
                )
                .fetchFirst() != null;
    }
}
