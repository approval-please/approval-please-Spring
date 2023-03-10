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

    public final ListPath<com.umc.approval.domain.approval.entity.Approval, com.umc.approval.domain.approval.entity.QApproval> approvals = this.<com.umc.approval.domain.approval.entity.Approval, com.umc.approval.domain.approval.entity.QApproval>createList("approvals", com.umc.approval.domain.approval.entity.Approval.class, com.umc.approval.domain.approval.entity.QApproval.class, PathInits.DIRECT2);

    public final EnumPath<com.umc.approval.global.type.CategoryType> category = createEnum("category", com.umc.approval.global.type.CategoryType.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.umc.approval.domain.image.entity.Image, com.umc.approval.domain.image.entity.QImage> images = this.<com.umc.approval.domain.image.entity.Image, com.umc.approval.domain.image.entity.QImage>createList("images", com.umc.approval.domain.image.entity.Image.class, com.umc.approval.domain.image.entity.QImage.class, PathInits.DIRECT2);

    public final ListPath<com.umc.approval.domain.like.entity.Like, com.umc.approval.domain.like.entity.QLike> likes = this.<com.umc.approval.domain.like.entity.Like, com.umc.approval.domain.like.entity.QLike>createList("likes", com.umc.approval.domain.like.entity.Like.class, com.umc.approval.domain.like.entity.QLike.class, PathInits.DIRECT2);

    public final com.umc.approval.domain.link.entity.QLink link;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final BooleanPath notification = createBoolean("notification");

    public final ListPath<com.umc.approval.domain.scrap.entity.Scrap, com.umc.approval.domain.scrap.entity.QScrap> scraps = this.<com.umc.approval.domain.scrap.entity.Scrap, com.umc.approval.domain.scrap.entity.QScrap>createList("scraps", com.umc.approval.domain.scrap.entity.Scrap.class, com.umc.approval.domain.scrap.entity.QScrap.class, PathInits.DIRECT2);

    public final NumberPath<Integer> state = createNumber("state", Integer.class);

    public final ListPath<com.umc.approval.domain.tag.entity.Tag, com.umc.approval.domain.tag.entity.QTag> tags = this.<com.umc.approval.domain.tag.entity.Tag, com.umc.approval.domain.tag.entity.QTag>createList("tags", com.umc.approval.domain.tag.entity.Tag.class, com.umc.approval.domain.tag.entity.QTag.class, PathInits.DIRECT2);

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
        this.link = inits.isInitialized("link") ? new com.umc.approval.domain.link.entity.QLink(forProperty("link"), inits.get("link")) : null;
        this.user = inits.isInitialized("user") ? new com.umc.approval.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

