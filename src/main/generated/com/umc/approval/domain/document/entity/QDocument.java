package com.umc.approval.domain.document.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDocument is a Querydsl query type for Document
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDocument extends EntityPathBase<Document> {

    private static final long serialVersionUID = 1101220706L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDocument document = new QDocument("document");

    public final com.umc.approval.domain.QBaseTimeEntity _super = new com.umc.approval.domain.QBaseTimeEntity(this);

    public final EnumPath<com.umc.approval.global.type.CategoryType> category = createEnum("category", com.umc.approval.global.type.CategoryType.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath linkUrl = createString("linkUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final BooleanPath notification = createBoolean("notification");

    public final NumberPath<Integer> state = createNumber("state", Integer.class);

    public final StringPath title = createString("title");

    public final com.umc.approval.domain.user.entity.QUser user;

    public final NumberPath<Long> view = createNumber("view", Long.class);

    public QDocument(String variable) {
        this(Document.class, forVariable(variable), INITS);
    }

    public QDocument(Path<? extends Document> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDocument(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDocument(PathMetadata metadata, PathInits inits) {
        this(Document.class, metadata, inits);
    }

    public QDocument(Class<? extends Document> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.umc.approval.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

