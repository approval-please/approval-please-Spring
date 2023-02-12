package com.umc.approval.domain.document.entity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.approval.global.type.CategoryType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static com.umc.approval.domain.document.entity.QDocument.document;
import static com.umc.approval.domain.tag.entity.QTag.tag1;

@Slf4j
@Repository
public class DocumentRepositoryImpl implements DocumentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DocumentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Document> findAllByQuery(String query, Integer isTag, Integer category, Integer state, Integer sortBy) {
        NumberExpression<Integer> date = new CaseBuilder()
                .when(document.createdAt.after(LocalDate.now().minusDays(7).atTime(LocalTime.MIN)))
                .then(1)
                .otherwise(0);
        if (isTag == 1) {
            log.debug("Query Hash : " + query + " / " + query.substring(3));
            String withoutShapeQuery = query.substring(3);
            return queryFactory
                    .select(document)
                    .from(tag1)
                    .innerJoin(tag1.document, document)
                    .leftJoin(document.likes)
                    .leftJoin(document.tags)
                    .leftJoin(document.link).fetchJoin()
                    .leftJoin(document.approvals)
                    .leftJoin(document.images)
                    .where(
                            tagEq(withoutShapeQuery),
                            categoryEq(category),
                            stateEq(state)
                    )
                    .distinct()
                    .orderBy(sortBy == 0 ? document.createdAt.desc() : date.desc(),
                            document.likes.size().add(document.comments.size()).add(document.view).desc())
                    .fetch();
        } else {
            return queryFactory
                    .selectFrom(document)
                    .leftJoin(document.likes)
                    .leftJoin(document.tags)
                    .leftJoin(document.comments)
                    .leftJoin(document.link).fetchJoin()
                    .leftJoin(document.approvals)
                    .leftJoin(document.images)
                    .where(
                            titleLike(query).or(contentLike(query)),
                            categoryEq(category),
                            stateEq(state)
                    )
                    .distinct()
                    .orderBy(sortBy == 0 ? document.createdAt.desc() : date.desc(),
                            document.likes.size().add(document.comments.size()).add(document.view).desc())
                    .fetch();
        }
    }

    @Override
    public Page<Document> findAllByQueryPaging(String query, Integer isTag, Integer category, Integer state, Integer sortBy, Pageable pageable) {
        List<Document> documents;
        JPAQuery<Long> countQuery;
        if (isTag == 1) {
            documents = queryFactory
                    .select(document)
                    .from(tag1)
                    .innerJoin(tag1.document, document)
                    .leftJoin(document.likes)
                    .leftJoin(document.tags)
                    .leftJoin(document.link).fetchJoin()
                    .leftJoin(document.approvals)
                    .leftJoin(document.images)
                    .where(
                            tagEq(withoutShapeQuery),
                            categoryEq(category),
                            stateEq(state)
                    )
                    .distinct()
                    .orderBy(sortBy == 0 ? document.createdAt.desc() : document.likes.size().desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .fetch();

            countQuery = queryFactory
                    .select(document.count())
                    .from(tag1)
                    .join(tag1.document, document)
                    .where(
                            tagEq(query),
                            categoryEq(category),
                            stateEq(state)
                    )
                    .distinct();
        } else {
            documents = queryFactory
                    .selectFrom(document)
                    .leftJoin(document.likes)
                    .leftJoin(document.tags)
                    .leftJoin(document.link).fetchJoin()
                    .leftJoin(document.approvals)
                    .leftJoin(document.images)
                    .where(
                            titleLike(query).or(contentLike(query)),
                            categoryEq(category),
                            stateEq(state)
                    )
                    .distinct()
                    .orderBy(sortBy == 0 ? document.createdAt.desc() : document.likes.size().desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .fetch();

            countQuery = queryFactory
                    .select(document.count())
                    .from(document)
                    .where(
                            titleLike(query).or(contentLike(query)),
                            categoryEq(category),
                            stateEq(state)
                    )
                    .distinct();
        }
        return PageableExecutionUtils.getPage(documents, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Document> findAllByOption(Integer category, Integer state, Integer sortBy) {
        NumberExpression<Integer> date = new CaseBuilder()
                .when(document.createdAt.after(LocalDate.now().minusDays(7).atTime(LocalTime.MIN)))
                .then(1)
                .otherwise(0);
        return queryFactory
                .selectFrom(document)
                .leftJoin(document.likes)
                .leftJoin(document.tags)
                .leftJoin(document.comments)
                .leftJoin(document.link).fetchJoin()
                .leftJoin(document.approvals)
                .leftJoin(document.images)
                .where(
                        categoryEq(category),
                        stateEq(state)
                )
                .distinct()
                .orderBy(sortBy == null ? document.createdAt.desc() :
                                date.desc(),
                        (sortBy == null || sortBy == 0) ?
                                document.likes.size().add(document.comments.size()).add(document.view).desc() :
                                document.approvals.size().asc()
                )
                .fetch();
    }

    @Override
    public List<Document> findAllByLikedCategories(List<CategoryType> categories, Integer state, Integer sortBy) {
        NumberExpression<Integer> date = new CaseBuilder()
                .when(document.createdAt.after(LocalDate.now().minusDays(7).atTime(LocalTime.MIN)))
                .then(1)
                .otherwise(0);
        return queryFactory
                .selectFrom(document)
                .leftJoin(document.likes)
                .leftJoin(document.tags)
                .leftJoin(document.comments)
                .leftJoin(document.link).fetchJoin()
                .leftJoin(document.approvals)
                .leftJoin(document.images)
                .where(
                        document.category.in(categories),
                        stateEq(state)
                )
                .distinct()
                .orderBy(sortBy == null ? document.createdAt.desc() :
                                date.desc(),
                        (sortBy == null || sortBy == 0) ?
                                document.likes.size().add(document.comments.size()).add(document.view).desc() :
                                document.approvals.size().asc()
                )
                .fetch();
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

    private BooleanExpression stateEq(Integer state) {
        return state == null ? null : document.state.eq(state);
    }

    private BooleanExpression titleLike(String query) {
        return document.title.contains(query);
    }

    private BooleanExpression contentLike(String query) {
        return document.content.contains(query);
    }
}
