package com.umc.approval.domain.toktok.entity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.approval.global.type.CategoryType;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static com.umc.approval.domain.document.entity.QDocument.document;
import static com.umc.approval.domain.tag.entity.QTag.tag1;
import static com.umc.approval.domain.toktok.entity.QToktok.toktok;
import static com.umc.approval.domain.vote.entity.QVote.vote;

public class ToktokRepositoryImpl implements ToktokRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public ToktokRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Toktok> findAllByQuery(String query, Integer isTag, Integer category, Integer sortBy) {
        if (isTag == 1) {
            return queryFactory
                    .select(toktok)
                    .from(tag1)
                    .innerJoin(tag1.toktok, toktok)
                    .innerJoin(toktok.user).fetchJoin()
                    .leftJoin(toktok.likes)
                    .leftJoin(toktok.tags)
                    .leftJoin(toktok.links)
                    .leftJoin(toktok.images)
                    .leftJoin(toktok.comments)
                    .leftJoin(toktok.vote, vote)
                    .leftJoin(vote.userVotes)
                    .where(
                            tagEq(query),
                            categoryEq(category)
                    )
                    .distinct()
                    .orderBy(sortBy == 0 ? toktok.createdAt.desc() : toktok.likes.size().desc())
                    .fetch();
        } else {
            return queryFactory
                    .selectFrom(toktok)
                    .innerJoin(toktok.user).fetchJoin()
                    .leftJoin(toktok.likes)
                    .leftJoin(toktok.tags)
                    .leftJoin(toktok.links)
                    .leftJoin(toktok.images)
                    .leftJoin(toktok.comments)
                    .leftJoin(toktok.vote, vote)
                    .leftJoin(vote.userVotes)
                    .where(
                            contentLike(query),
                            categoryEq(category)
                    )
                    .distinct()
                    .orderBy(sortBy == 0 ? toktok.createdAt.desc() : toktok.likes.size().desc())
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
        return category == null ? null : toktok.category.eq(categoryType);
    }

    private BooleanExpression contentLike(String query) {
        return toktok.content.contains(query);
    }
}
