package com.umc.approval.domain.comment.entity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public List<Comment> findAllByPost(Long documentId, Long toktokId, Long reportId) {
        QComment cc = new QComment("cc");

        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.childComment, cc) // 대댓글 함께 조회
                .leftJoin(cc.user) // 대댓글 유저 함께 조회
                .leftJoin(cc.likes) // 대댓글 좋아요 함께 조회
                .innerJoin(comment.user).fetchJoin() // 댓글 유저 함께 조회
                .leftJoin(comment.likes) // 댓글 좋아요 함께 조회
                .where(
                        documentEq(documentId),
                        toktokEq(toktokId),
                        reportEq(reportId),
                        comment.parentComment.id.isNull() // 대댓글이 아닌 것만
                )
                .distinct()
                .fetch();
    }

    @Override
    public Page<Comment> findAllByPostPaging(Pageable pageable, Long documentId, Long toktokId, Long reportId) {

        QComment cc = new QComment("cc");

        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .leftJoin(comment.childComment, cc) // 대댓글 함께 조회
                .leftJoin(cc.user) // 대댓글 유저 함께 조회
                .leftJoin(cc.likes) // 대댓글 좋아요 함께 조회
                .innerJoin(comment.user) // 댓글 유저 함께 조회
                .leftJoin(comment.likes) // 댓글 좋아요 함께 조회
                .where(
                        documentEq(documentId),
                        toktokEq(toktokId),
                        reportEq(reportId),
                        comment.parentComment.id.isNull() // 대댓글이 아닌 것만
                )
                .distinct()
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(
                        documentEq(documentId),
                        toktokEq(toktokId),
                        reportEq(reportId),
                        comment.parentComment.id.isNull()
                );

        return PageableExecutionUtils.getPage(comments, pageable, countQuery::fetchOne);
    }

    @Override
    public Integer countByPost(Long documentId, Long toktokId, Long reportId) {
        return Math.toIntExact(queryFactory
                .select(comment.count())
                .where(
                        documentEq(documentId),
                        toktokEq(toktokId),
                        reportEq(reportId)
                )
                .from(comment)
                .fetchFirst());
    }

    private BooleanExpression documentEq(Long documentId) {
        return documentId == null ? null : comment.document.id.eq(documentId);
    }

    private BooleanExpression toktokEq(Long toktokId) {
        return toktokId == null ? null : comment.toktok.id.eq(toktokId);
    }

    private BooleanExpression reportEq(Long reportId) {
        return reportId == null ? null : comment.report.id.eq(reportId);
    }
}
