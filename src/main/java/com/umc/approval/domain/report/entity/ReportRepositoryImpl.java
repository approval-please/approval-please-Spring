package com.umc.approval.domain.report.entity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.approval.global.type.CategoryType;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static com.umc.approval.domain.document.entity.QDocument.document;
import static com.umc.approval.domain.report.entity.QReport.report;
import static com.umc.approval.domain.tag.entity.QTag.tag1;

public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public ReportRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Report> findAllByQuery(String query, Integer isTag, Integer category, Integer sortBy) {
        NumberExpression<Integer> date = new CaseBuilder()
                .when(report.createdAt.after(LocalDate.now().minusDays(7).atTime(LocalTime.MIN)))
                .then(1)
                .otherwise(0);
        if (isTag == 1) {
            return queryFactory
                    .select(report)
                    .from(tag1)
                    .innerJoin(tag1.report, report)
                    .innerJoin(report.document, document).fetchJoin()
                    .innerJoin(document.user).fetchJoin()
                    .leftJoin(report.likes)
                    .leftJoin(report.tags)
                    .leftJoin(document.tags)
                    .leftJoin(document.images)
                    .leftJoin(document.link).fetchJoin()
                    .leftJoin(report.links)
                    .leftJoin(report.images)
                    .leftJoin(report.comments)
                    .where(
                            tagEq(query),
                            categoryEq(category)
                    )
                    .distinct()
                    .orderBy(sortBy == 0 ? report.createdAt.desc() : date.desc(),
                            report.likes.size().add(report.comments.size()).add(report.view).desc())
                    .fetch();
        } else {
            return queryFactory
                    .selectFrom(report)
                    .innerJoin(report.document, document).fetchJoin()
                    .innerJoin(document.user).fetchJoin()
                    .leftJoin(report.likes)
                    .leftJoin(report.tags)
                    .leftJoin(document.tags)
                    .leftJoin(document.images)
                    .leftJoin(document.link).fetchJoin()
                    .leftJoin(report.links)
                    .leftJoin(report.images)
                    .leftJoin(report.comments)
                    .where(
                            contentLike(query),
                            categoryEq(category)
                    )
                    .distinct()
                    .orderBy(sortBy == 0 ? report.createdAt.desc() : date.desc(),
                            report.likes.size().add(report.comments.size()).add(report.view).desc())
                    .fetch();
        }
    }

    private BooleanExpression tagEq(String tag) {
        return tag1.tag.eq(tag);
    }

    private BooleanExpression categoryEq(Integer category) {
        CategoryType categoryType = null;
        if (category != null) {
            categoryType = Arrays.stream(CategoryType.values())
                    .filter(c -> c.getValue() == category)
                    .findAny().get();
        }
        return category == null ? null : document.category.eq(categoryType);
    }

    private BooleanExpression contentLike(String query) {
        return report.content.contains(query);
    }
}
