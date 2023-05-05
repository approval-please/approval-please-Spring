package com.umc.approval.domain.scrap.entity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.approval.domain.scrap.dto.ScrapDto;
import com.umc.approval.global.util.BooleanBuilderUtil;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.umc.approval.domain.scrap.entity.QScrap.scrap;

public class ScrapRepositoryImpl implements ScrapRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public ScrapRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Scrap> findByUserAndPost(Long userId, ScrapDto.Request request) {
        Scrap result = queryFactory
                .selectFrom(scrap)
                .where(
                        BooleanBuilderUtil.postEq(
                                scrap.document,
                                scrap.toktok,
                                scrap.report,
                                request.getDocumentId(),
                                request.getToktokId(),
                                request.getReportId()
                        ),
                        userEq(userId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private BooleanExpression userEq(Long userId) {
        return scrap.user.id.eq(userId);
    }
}
