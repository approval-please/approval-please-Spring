package com.umc.approval.domain.toktok.entity;

import com.umc.approval.domain.BaseTimeEntity;
import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.vote.entity.Vote;
import com.umc.approval.global.type.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
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
public class Toktok extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "toktok_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(STRING)
    @Column(nullable = false)
    private CategoryType category;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @Column(columnDefinition = "longtext", nullable = false)
    private String content;

    @Column(nullable = false)
    private Long view;

    @Column(nullable = false)
    private boolean notification;

    @Builder.Default
    @OneToMany(fetch = LAZY, mappedBy = "toktok")
    private List<Tag> tags = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = LAZY, mappedBy = "toktok")
    private List<Link> links = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = LAZY, mappedBy = "toktok")
    private List<Like> likes = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = LAZY, mappedBy = "toktok")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = LAZY, mappedBy = "toktok")
    private List<Image> images = new ArrayList<>();

    public void update(ToktokDto.PostToktokRequest request, CategoryType categoryType, Vote vote) {
        this.category = categoryType;
        this.content = request.getContent();
        this.vote = vote;
    }

    public void deleteVote() {
        this.vote = null;
    }

    public void setNotification(Boolean onOff){
        this.notification = onOff;
    }
}
