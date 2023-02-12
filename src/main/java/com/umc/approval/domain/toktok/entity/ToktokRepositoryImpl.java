package com.umc.approval.domain.toktok.entity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.global.type.CategoryType;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.umc.approval.domain.document.entity.QDocument.document;
import static com.umc.approval.domain.tag.entity.QTag.tag1;
import static com.umc.approval.domain.toktok.entity.QToktok.toktok;
import static com.umc.approval.domain.vote.entity.QVote.vote;

@Transactional
public class ToktokRepositoryImpl implements ToktokRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public ToktokRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Toktok> findAllByQuery(String query, Integer isTag, Integer category, Integer sortBy) {
        NumberExpression<Integer> date = new CaseBuilder()
                .when(toktok.createdAt.after(LocalDate.now().minusDays(7).atTime(LocalTime.MIN)))
                .then(1)
                .otherwise(0);
        if (isTag == 1) {
            String withoutShapeQuery = query.substring(1);
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
                            tagEq(withoutShapeQuery),
                            categoryEq(category)
                    )
                    .distinct()
                    .orderBy(sortBy == 0 ? toktok.createdAt.desc() : date.desc(),
                            toktok.likes.size().add(toktok.comments.size()).add(toktok.view).desc())
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
                    .orderBy(sortBy == 0 ? toktok.createdAt.desc() : date.desc(),
                            toktok.likes.size().add(toktok.comments.size()).add(toktok.view).desc())
                    .fetch();
        }
    }

    @Override
    public List<Toktok> findAllByOption(Long userId, List<Follow> follows, Integer sortBy) {
        NumberExpression<Integer> date = new CaseBuilder()
                .when(toktok.createdAt.after(LocalDate.now().minusDays(7).atTime(LocalTime.MIN)))
                .then(1)
                .otherwise(0);
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
                        followingEq(follows, sortBy),
                        userEq(userId, sortBy)
                )
                .distinct()
                .orderBy(sortBy != null && sortBy == 0 ? date.desc() : toktok.createdAt.desc(),
                        toktok.likes.size().add(toktok.comments.size()).add(toktok.view).desc())
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
        return category == null ? null : toktok.category.eq(categoryType);
    }

    private BooleanExpression contentLike(String query) {
        return toktok.content.contains(query);
    }

    private BooleanExpression followingEq(List<Follow> follows, Integer sortBy) {
        return sortBy == null || sortBy != 1 ? null
                : toktok.user.in(follows.stream().map(Follow::getToUser).collect(Collectors.toList()));
    }

    private BooleanExpression userEq(Long userId, Integer sortBy) {
        return userId == null || sortBy == null || sortBy != 2 ? null
                : toktok.user.id.eq(userId);
    }
}
