package com.umc.approval.domain.document.entity;

import com.umc.approval.domain.BaseTimeEntity;
import com.umc.approval.domain.approval.entity.Approval;
import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.scrap.entity.Scrap;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.global.type.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @Column(nullable = false)
    private Boolean notification;

    @OneToMany(fetch = LAZY, mappedBy = "document")
    private List<Tag> tags;

    @OneToMany(fetch = LAZY, mappedBy = "document")
    private List<Image> images;

    @OneToMany(fetch = LAZY, mappedBy = "document")
    private List<Approval> approvals;

    @OneToOne(fetch = LAZY, mappedBy = "document")
    private Link link;

    @OneToMany(fetch = LAZY, mappedBy = "document")
    private List<Like> likes;

    @OneToMany(fetch = LAZY, mappedBy = "document")
    private List<Scrap> scraps;

    @OneToMany(fetch = LAZY, mappedBy = "document")
    private List<Comment> comments;

    public void update(CategoryType category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public void setNotification(Boolean onOff){
        this.notification = onOff;
    }
}
