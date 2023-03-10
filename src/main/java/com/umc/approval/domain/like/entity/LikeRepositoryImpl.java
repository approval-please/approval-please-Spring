package com.umc.approval.domain.like.entity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.approval.domain.like.dto.LikeDto;
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
    public List<Like> findAllByPost(Long documentId, Long toktokId, Long reportId) {
        return queryFactory
                .selectFrom(like)
                .innerJoin(like.user, user).fetchJoin()
                .where(
                        documentEq(documentId),
                        toktokEq(toktokId),
                        reportEq(reportId)
                )
                .fetch();
    }

    @Override
    public Page<Like> findAllByPostPaging(Pageable pageable, LikeDto.Request requestDto) {
        List<Like> likes = queryFactory
                .selectFrom(like)
                .innerJoin(like.user, user).fetchJoin()
                .where(
                        documentEq(requestDto.getDocumentId()),
                        toktokEq(requestDto.getToktokId()),
                        reportEq(requestDto.getReportId())
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(like.count())
                .from(like)
                .where(
                        documentEq(requestDto.getDocumentId()),
                        toktokEq(requestDto.getToktokId()),
                        reportEq(requestDto.getReportId())
                );

        return PageableExecutionUtils.getPage(likes, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<Like> findByUserAndPost(Long userId, LikeDto.Request requestDto) {
        Like result = queryFactory
                .selectFrom(like)
                .where(
                        userEq(userId),
                        documentEq(requestDto.getDocumentId()),
                        toktokEq(requestDto.getToktokId()),
                        reportEq(requestDto.getReportId()),
                        commentEq(requestDto.getCommentId())
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private BooleanExpression userEq(Long userId) {
        return like.user.id.eq(userId);
    }

    private BooleanExpression documentEq(Long documentId) {
        return documentId == null ? null : like.document.id.eq(documentId);
    }

    private BooleanExpression toktokEq(Long toktokId) {
        return toktokId == null ? null : like.toktok.id.eq(toktokId);
    }

    private BooleanExpression reportEq(Long reportId) {
        return reportId == null ? null : like.report.id.eq(reportId);
    }

    private BooleanExpression commentEq(Long commentId) {
        return commentId == null ? null : like.comment.id.eq(commentId);
    }
}
