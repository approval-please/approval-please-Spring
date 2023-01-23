package com.umc.approval.domain.vote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserVote is a Querydsl query type for UserVote
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserVote extends EntityPathBase<UserVote> {

    private static final long serialVersionUID = 1398952683L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserVote userVote = new QUserVote("userVote");

    public final com.umc.approval.domain.QBaseTimeEntity _super = new com.umc.approval.domain.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.umc.approval.domain.user.entity.QUser user;

    public final QVote vote;

    public final QVoteOption voteOption;

    public QUserVote(String variable) {
        this(UserVote.class, forVariable(variable), INITS);
    }

    public QUserVote(Path<? extends UserVote> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserVote(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserVote(PathMetadata metadata, PathInits inits) {
        this(UserVote.class, metadata, inits);
    }

    public QUserVote(Class<? extends UserVote> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.umc.approval.domain.user.entity.QUser(forProperty("user")) : null;
        this.vote = inits.isInitialized("vote") ? new QVote(forProperty("vote")) : null;
        this.voteOption = inits.isInitialized("voteOption") ? new QVoteOption(forProperty("voteOption"), inits.get("voteOption")) : null;
    }

}

