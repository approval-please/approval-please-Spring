package com.umc.approval.domain.accuse.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccuse is a Querydsl query type for Accuse
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccuse extends EntityPathBase<Accuse> {

    private static final long serialVersionUID = -1892283848L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAccuse accuse = new QAccuse("accuse");

    public final com.umc.approval.domain.QBaseTimeEntity _super = new com.umc.approval.domain.QBaseTimeEntity(this);

    public final com.umc.approval.domain.user.entity.QUser accuseUser;

    public final com.umc.approval.domain.comment.entity.QComment comment;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final com.umc.approval.domain.document.entity.QDocument document;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.umc.approval.domain.report.entity.QReport report;

    public final com.umc.approval.domain.toktok.entity.QToktok toktok;

    public final com.umc.approval.domain.user.entity.QUser user;

    public QAccuse(String variable) {
        this(Accuse.class, forVariable(variable), INITS);
    }

    public QAccuse(Path<? extends Accuse> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAccuse(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAccuse(PathMetadata metadata, PathInits inits) {
        this(Accuse.class, metadata, inits);
    }

    public QAccuse(Class<? extends Accuse> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.accuseUser = inits.isInitialized("accuseUser") ? new com.umc.approval.domain.user.entity.QUser(forProperty("accuseUser")) : null;
        this.comment = inits.isInitialized("comment") ? new com.umc.approval.domain.comment.entity.QComment(forProperty("comment"), inits.get("comment")) : null;
        this.document = inits.isInitialized("document") ? new com.umc.approval.domain.document.entity.QDocument(forProperty("document"), inits.get("document")) : null;
        this.report = inits.isInitialized("report") ? new com.umc.approval.domain.report.entity.QReport(forProperty("report"), inits.get("report")) : null;
        this.toktok = inits.isInitialized("toktok") ? new com.umc.approval.domain.toktok.entity.QToktok(forProperty("toktok"), inits.get("toktok")) : null;
        this.user = inits.isInitialized("user") ? new com.umc.approval.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

