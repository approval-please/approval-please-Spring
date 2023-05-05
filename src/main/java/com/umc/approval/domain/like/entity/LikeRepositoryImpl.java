package com.umc.approval.domain.like.entity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.global.util.BooleanBuilderUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.umc.approval.domain.like.entity.QLike.like;
import static com.umc.approval.domain.user.entity.QUser.user;

public class LikeRepositoryImpl implements LikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public LikeRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Like> findAllByPost(BooleanBuilderUtil.PostIds postIds) {
        return queryFactory
                .selectFrom(like)
                .innerJoin(like.user, user).fetchJoin()
                .where(BooleanBuilderUtil.postEq(like.document, like.toktok, like.report, postIds))
                .fetch();
    }

    @Override
    public Page<Like> findAllByPostPaging(Pageable pageable, BooleanBuilderUtil.PostIds postIds) {
        List<Like> likes = queryFactory
                .selectFrom(like)
                .innerJoin(like.user, user).fetchJoin()
                .where(BooleanBuilderUtil.postEq(like.document, like.toktok, like.report, postIds))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(like.count())
                .from(like)
                .where(BooleanBuilderUtil.postEq(like.document, like.toktok, like.report, postIds));

        return PageableExecutionUtils.getPage(likes, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<Like> findByUserAndPost(Long userId, LikeDto.Request requestDto) {
        Like result = queryFactory
                .selectFrom(like)
                .where(
                        BooleanBuilderUtil.postEq(
                                like.document,
                                like.toktok,
                                like.report,
                                requestDto.getDocumentId(),
                                requestDto.getToktokId(),
                                requestDto.getReportId()
                        ),
                        userEq(userId),
                        commentEq(requestDto.getCommentId())
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private BooleanExpression userEq(Long userId) {
        return like.user.id.eq(userId);
    }

    private BooleanExpression commentEq(Long commentId) {
        return commentId == null ? null : like.comment.id.eq(commentId);
    }
}
