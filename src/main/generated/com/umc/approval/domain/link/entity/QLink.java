package com.umc.approval.domain.link.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLink is a Querydsl query type for Link
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLink extends EntityPathBase<Link> {

    private static final long serialVersionUID = 836283808L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLink link = new QLink("link");

    public final com.umc.approval.domain.QBaseTimeEntity _super = new com.umc.approval.domain.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final com.umc.approval.domain.document.entity.QDocument document;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.umc.approval.domain.report.entity.QReport report;

    public final StringPath title = createString("title");

    public final com.umc.approval.domain.toktok.entity.QToktok toktok;

    public final StringPath url = createString("url");

    public QLink(String variable) {
        this(Link.class, forVariable(variable), INITS);
    }

    public QLink(Path<? extends Link> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLink(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLink(PathMetadata metadata, PathInits inits) {
        this(Link.class, metadata, inits);
    }

    public QLink(Class<? extends Link> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.document = inits.isInitialized("document") ? new com.umc.approval.domain.document.entity.QDocument(forProperty("document"), inits.get("document")) : null;
        this.report = inits.isInitialized("report") ? new com.umc.approval.domain.report.entity.QReport(forProperty("report"), inits.get("report")) : null;
        this.toktok = inits.isInitialized("toktok") ? new com.umc.approval.domain.toktok.entity.QToktok(forProperty("toktok"), inits.get("toktok")) : null;
    }

}

