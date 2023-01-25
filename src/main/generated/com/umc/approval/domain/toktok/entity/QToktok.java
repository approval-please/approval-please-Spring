package com.umc.approval.domain.toktok.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QToktok is a Querydsl query type for Toktok
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QToktok extends EntityPathBase<Toktok> {

    private static final long serialVersionUID = -300904980L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QToktok toktok = new QToktok("toktok");

    public final com.umc.approval.domain.QBaseTimeEntity _super = new com.umc.approval.domain.QBaseTimeEntity(this);

    public final EnumPath<com.umc.approval.global.type.CategoryType> category = createEnum("category", com.umc.approval.global.type.CategoryType.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final BooleanPath notification = createBoolean("notification");

    public final com.umc.approval.domain.user.entity.QUser user;

    public final NumberPath<Long> view = createNumber("view", Long.class);

    public final com.umc.approval.domain.vote.entity.QVote vote;

    public QToktok(String variable) {
        this(Toktok.class, forVariable(variable), INITS);
    }

    public QToktok(Path<? extends Toktok> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QToktok(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QToktok(PathMetadata metadata, PathInits inits) {
        this(Toktok.class, metadata, inits);
    }

    public QToktok(Class<? extends Toktok> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.umc.approval.domain.user.entity.QUser(forProperty("user")) : null;
        this.vote = inits.isInitialized("vote") ? new com.umc.approval.domain.vote.entity.QVote(forProperty("vote")) : null;
    }

}

