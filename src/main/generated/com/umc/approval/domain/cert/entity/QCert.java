package com.umc.approval.domain.cert.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCert is a Querydsl query type for Cert
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCert extends EntityPathBase<Cert> {

    private static final long serialVersionUID = -967281932L;

    public static final QCert cert = new QCert("cert");

    public final com.umc.approval.domain.QBaseTimeEntity _super = new com.umc.approval.domain.QBaseTimeEntity(this);

    public final StringPath certNumber = createString("certNumber");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isChecked = createBoolean("isChecked");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath phoneNumber = createString("phoneNumber");

    public QCert(String variable) {
        super(Cert.class, forVariable(variable));
    }

    public QCert(Path<? extends Cert> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCert(PathMetadata metadata) {
        super(Cert.class, metadata);
    }

}

