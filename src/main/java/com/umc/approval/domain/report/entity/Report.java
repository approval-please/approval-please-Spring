package com.umc.approval.domain.report.entity;

import com.umc.approval.domain.BaseTimeEntity;
import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.tag.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.List;

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

    @OneToMany(fetch = LAZY, mappedBy = "report")
    private List<Tag> tags;

    @OneToMany(fetch = LAZY, mappedBy = "report")
    private List<Image> images;

    @OneToMany(fetch = LAZY, mappedBy = "report")
    private List<Link> links;

    @OneToMany(fetch = LAZY, mappedBy = "report")
    private List<Like> likes;

    @OneToMany(fetch = LAZY, mappedBy = "report")
    private List<Comment> comments;

    public void update(ReportDto.ReportRequest request, Document document) {
        this.document = document;
        this.content = request.getContent();
    }
}