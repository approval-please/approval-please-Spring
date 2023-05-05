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

    public static BooleanBuilder postEq(QDocument document, QToktok toktok, QReport report, PostIds postIds) {
        return documentEq(document, postIds.getDocumentId())
                .and(toktokEq(toktok, postIds.getToktokId()))
                .and(reportEq(report, postIds.getReportId()));
    }

    public static BooleanBuilder postEq(
            QDocument document,
            QToktok toktok,
            QReport report,
            Long documentId,
            Long toktokId,
            Long reportId
    ) {
        return documentEq(document, documentId)
                .and(toktokEq(toktok, toktokId))
                .and(reportEq(report, reportId));
    }

    private static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> be) {
        try {
            return new BooleanBuilder(be.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }

    private static BooleanBuilder documentEq(QDocument document, Long documentId) {
        return nullSafeBuilder(() -> document.id.eq(documentId));
    }

    private static BooleanBuilder toktokEq(QToktok toktok, Long toktokId) {
        return nullSafeBuilder(() -> toktok.id.eq(toktokId));
    }

    private static BooleanBuilder reportEq(QReport report, Long reportId) {
        return nullSafeBuilder(() -> report.id.eq(reportId));
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
