package com.umc.approval.global.util;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.umc.approval.domain.document.entity.QDocument;
import com.umc.approval.domain.report.entity.QReport;
import com.umc.approval.domain.toktok.entity.QToktok;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Supplier;


public class BooleanBuilderUtil {

    public static BooleanBuilder postEq(QDocument document, QToktok toktok ,QReport report, PostIds postIds) {
        return documentEq(document, postIds).and(toktokEq(toktok, postIds)).and(reportEq(report, postIds));
    }

    private static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> be) {
        try {
            return new BooleanBuilder(be.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }

    private static BooleanBuilder documentEq(QDocument document, PostIds postIds) {
        return nullSafeBuilder(() -> document.id.eq(postIds.getDocumentId()));
    }

    private static BooleanBuilder toktokEq(QToktok toktok, PostIds postIds) {
        return nullSafeBuilder(() -> toktok.id.eq(postIds.getToktokId()));
    }

    private static BooleanBuilder reportEq(QReport report, PostIds postIds) {
        return nullSafeBuilder(() -> report.id.eq(postIds.getReportId()));
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PostIds {
        Long documentId;
        Long toktokId;
        Long reportId;
    }
}
