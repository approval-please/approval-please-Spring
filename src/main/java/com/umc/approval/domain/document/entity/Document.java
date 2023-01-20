package com.umc.approval.domain.document.entity;

import com.umc.approval.domain.BaseTimeEntity;
import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.like_category.entity.LikeCategory;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.global.type.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;


@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Document extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "document_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(STRING)
    @Column(nullable = false)
    private CategoryType category;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "longtext", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer state;

    @Column(nullable = false)
    private Long view;

    @Column(columnDefinition = "tinyint(1) default 1", nullable = false)
    private Boolean notification;

    @Column(name = "link_url")
    private String linkUrl;

    public void update(CategoryType category, String title, String content, String linkUrl) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.linkUrl = linkUrl;
    }
}
