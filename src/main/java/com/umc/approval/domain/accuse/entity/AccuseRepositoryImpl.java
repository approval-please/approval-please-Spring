package com.umc.approval.domain.accuse.entity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.approval.domain.accuse.dto.AccuseDto;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.umc.approval.domain.accuse.entity.QAccuse.accuse;

public class AccuseRepositoryImpl implements AccuseRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public AccuseRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Accuse> findByUserAndPost(Long userId, AccuseDto.Request accuseRequest) {
        Accuse result = queryFactory
                .selectFrom(accuse)
                .where(
                        userEq(userId),
                        accuseUserEq(accuseRequest.getAccuseUserId()),
                        documentEq(accuseRequest.getDocumentId()),
                        toktokEq(accuseRequest.getToktokId()),
                        reportEq(accuseRequest.getReportId()),
                        commentEq(accuseRequest.getCommentId())
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private BooleanExpression userEq(Long userId) {
        return accuse.user.id.eq(userId);
    }

    private BooleanExpression accuseUserEq(Long accuseUserId) {
        return accuseUserId == null ? null : accuse.accuseUser.id.eq(accuseUserId);
    }

    private BooleanExpression documentEq(Long documentId) {
        return documentId == null ? null : accuse.document.id.eq(documentId);
    }

    private BooleanExpression toktokEq(Long toktokId) {
        return toktokId == null ? null : accuse.toktok.id.eq(toktokId);
    }

    private BooleanExpression reportEq(Long reportId) {
        return reportId == null ? null : accuse.report.id.eq(reportId);
    }

    private BooleanExpression commentEq(Long commentId) {
        return commentId == null ? null : accuse.comment.id.eq(commentId);
    }

}
