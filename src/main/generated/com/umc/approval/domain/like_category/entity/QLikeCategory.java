package com.umc.approval.domain.like_category.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLikeCategory is a Querydsl query type for LikeCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLikeCategory extends EntityPathBase<LikeCategory> {

    private static final long serialVersionUID = 1185523273L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLikeCategory likeCategory = new QLikeCategory("likeCategory");

    public final com.umc.approval.domain.QBaseTimeEntity _super = new com.umc.approval.domain.QBaseTimeEntity(this);

    public final EnumPath<com.umc.approval.global.type.CategoryType> category = createEnum("category", com.umc.approval.global.type.CategoryType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.umc.approval.domain.user.entity.QUser user;

    public QLikeCategory(String variable) {
        this(LikeCategory.class, forVariable(variable), INITS);
    }

    public QLikeCategory(Path<? extends LikeCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLikeCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLikeCategory(PathMetadata metadata, PathInits inits) {
        this(LikeCategory.class, metadata, inits);
    }

    public QLikeCategory(Class<? extends LikeCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.umc.approval.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

