package com.umc.approval.domain.report.entity;

import com.umc.approval.domain.BaseTimeEntity;
import com.umc.approval.domain.document.entity.Document;
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
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(nullable = false, columnDefinition = "longtext")
    private String content;

    @Column(nullable = false)
    private Long view;

    @Column(nullable = false)
    private Boolean notification;
}
