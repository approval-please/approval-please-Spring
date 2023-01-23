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
    public Page<Like> findAllByPost(Pageable pageable, LikeDto.Request requestDto) {
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

    private BooleanExpression documentEq(Long documentId) {
        return documentId == null ? null : like.document.id.eq(documentId);
    }

    private BooleanExpression toktokEq(Long toktokId) {
        return toktokId == null ? null : like.toktok.id.eq(toktokId);
    }

    private BooleanExpression reportEq(Long reportId) {
        return reportId == null ? null : like.report.id.eq(reportId);
    }
}
