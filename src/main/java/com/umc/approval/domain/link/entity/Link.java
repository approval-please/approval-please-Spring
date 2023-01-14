package com.umc.approval.domain.link.entity;

import com.umc.approval.domain.BaseTimeEntity;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.toktok.entity.Toktok;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Link extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "link_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "toktok_id")
    private Toktok toktok;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @Column(nullable = false)
    private String linkUrl;
}
