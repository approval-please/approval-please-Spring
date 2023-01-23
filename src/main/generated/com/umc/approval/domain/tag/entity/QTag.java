package com.umc.approval.domain.tag.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTag is a Querydsl query type for Tag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTag extends EntityPathBase<Tag> {

    private static final long serialVersionUID = -1020449838L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTag tag1 = new QTag("tag1");

    public final com.umc.approval.domain.document.entity.QDocument document;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.umc.approval.domain.report.entity.QReport report;

    public final StringPath tag = createString("tag");

    public final com.umc.approval.domain.toktok.entity.QToktok toktok;

    public QTag(String variable) {
        this(Tag.class, forVariable(variable), INITS);
    }

    public QTag(Path<? extends Tag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTag(PathMetadata metadata, PathInits inits) {
        this(Tag.class, metadata, inits);
    }

    public QTag(Class<? extends Tag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.document = inits.isInitialized("document") ? new com.umc.approval.domain.document.entity.QDocument(forProperty("document"), inits.get("document")) : null;
        this.report = inits.isInitialized("report") ? new com.umc.approval.domain.report.entity.QReport(forProperty("report"), inits.get("report")) : null;
        this.toktok = inits.isInitialized("toktok") ? new com.umc.approval.domain.toktok.entity.QToktok(forProperty("toktok"), inits.get("toktok")) : null;
    }

}

