package com.umc.approval.domain.vote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVote is a Querydsl query type for Vote
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVote extends EntityPathBase<Vote> {

    private static final long serialVersionUID = -791344256L;

    public static final QVote vote = new QVote("vote");

    public final com.umc.approval.domain.QBaseTimeEntity _super = new com.umc.approval.domain.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAnonymous = createBoolean("isAnonymous");

    public final BooleanPath isEnd = createBoolean("isEnd");

    public final BooleanPath isSingle = createBoolean("isSingle");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath title = createString("title");

    public final ListPath<UserVote, QUserVote> userVotes = this.<UserVote, QUserVote>createList("userVotes", UserVote.class, QUserVote.class, PathInits.DIRECT2);

    public final ListPath<VoteOption, QVoteOption> voteOptions = this.<VoteOption, QVoteOption>createList("voteOptions", VoteOption.class, QVoteOption.class, PathInits.DIRECT2);

    public QVote(String variable) {
        super(Vote.class, forVariable(variable));
    }

    public QVote(Path<? extends Vote> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVote(PathMetadata metadata) {
        super(Vote.class, metadata);
    }

}

