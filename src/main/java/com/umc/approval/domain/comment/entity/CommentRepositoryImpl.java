package com.umc.approval.domain.comment.entity;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.approval.global.util.BooleanBuilderUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.support.PageableExecutionUtils;

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
                .distinct()
                .fetch();
    }

    @Override
    public Slice<Comment> findAllByPostSlice(Pageable pageable, BooleanBuilderUtil.PostIds postIds) {
        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .innerJoin(comment.user).fetchJoin() // 댓글 유저 함께 조회
                .where(
                        BooleanBuilderUtil.postEq(comment.document, comment.toktok, comment.report, postIds),
                        comment.parentComment.id.isNull() // 대댓글이 아닌 것만
                )
                .distinct()
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        return PageableExecutionUtils.getPage(comments, pageable, countQuery::fetchOne);
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
