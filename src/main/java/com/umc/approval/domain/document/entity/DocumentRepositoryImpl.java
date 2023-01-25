package com.umc.approval.domain.document.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

public class DocumentRepositoryImpl implements DocumentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public DocumentRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Document> findAllByQuery(String query, Integer category, Integer state, Integer sortBy, Pageable pageable) {
//        Boolean isTag = query.startsWith("#");
//
//        if (isTag) {
//
//        }
//        List<Document> documents = queryFactory
//                .selectFrom(document)
//                .leftJoin(document.tags)
//                .leftJoin(document.link)
//                .leftJoin(document.approvals)
//                .distinct()
//                .limit(pageable.getPageSize())
//                .where(
//
//                )
//                .offset(pageable.getOffset());
        return null;
    }
}
