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

    public final ListPath<com.umc.approval.domain.image.entity.Image, com.umc.approval.domain.image.entity.QImage> images = this.<com.umc.approval.domain.image.entity.Image, com.umc.approval.domain.image.entity.QImage>createList("images", com.umc.approval.domain.image.entity.Image.class, com.umc.approval.domain.image.entity.QImage.class, PathInits.DIRECT2);

    public final ListPath<com.umc.approval.domain.like.entity.Like, com.umc.approval.domain.like.entity.QLike> likes = this.<com.umc.approval.domain.like.entity.Like, com.umc.approval.domain.like.entity.QLike>createList("likes", com.umc.approval.domain.like.entity.Like.class, com.umc.approval.domain.like.entity.QLike.class, PathInits.DIRECT2);

    public final ListPath<com.umc.approval.domain.link.entity.Link, com.umc.approval.domain.link.entity.QLink> links = this.<com.umc.approval.domain.link.entity.Link, com.umc.approval.domain.link.entity.QLink>createList("links", com.umc.approval.domain.link.entity.Link.class, com.umc.approval.domain.link.entity.QLink.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final BooleanPath notification = createBoolean("notification");

    public final ListPath<com.umc.approval.domain.scrap.entity.Scrap, com.umc.approval.domain.scrap.entity.QScrap> scraps = this.<com.umc.approval.domain.scrap.entity.Scrap, com.umc.approval.domain.scrap.entity.QScrap>createList("scraps", com.umc.approval.domain.scrap.entity.Scrap.class, com.umc.approval.domain.scrap.entity.QScrap.class, PathInits.DIRECT2);

    public final ListPath<com.umc.approval.domain.tag.entity.Tag, com.umc.approval.domain.tag.entity.QTag> tags = this.<com.umc.approval.domain.tag.entity.Tag, com.umc.approval.domain.tag.entity.QTag>createList("tags", com.umc.approval.domain.tag.entity.Tag.class, com.umc.approval.domain.tag.entity.QTag.class, PathInits.DIRECT2);

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

