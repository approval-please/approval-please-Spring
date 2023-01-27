package com.umc.approval.domain.scrap.entity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.approval.domain.scrap.dto.ScrapDto;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.umc.approval.domain.scrap.entity.QScrap.scrap;

public class ScrapRepositoryImpl implements ScrapRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public ScrapRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Scrap> findByUserAndPost(Long userId, ScrapDto.Request scrapRequset) {
        Scrap result = queryFactory
                .selectFrom(scrap)
                .where(
                        userEq(userId),
                        documentEq(scrapRequset.getDocumentId()),
                        toktokEq(scrapRequset.getToktokId()),
                        reportEq(scrapRequset.getReportId())
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private BooleanExpression userEq(Long userId) {
        return scrap.user.id.eq(userId);
    }

    private BooleanExpression documentEq(Long documentId) {
        return documentId == null ? null : scrap.document.id.eq(documentId);
    }

    private BooleanExpression toktokEq(Long toktokId) {
        return toktokId == null ? null : scrap.toktok.id.eq(toktokId);
    }

    private BooleanExpression reportEq(Long reportId) {
        return reportId == null ? null : scrap.report.id.eq(reportId);
    }

}
