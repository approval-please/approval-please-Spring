package com.umc.approval.domain.comment.entity;

import com.umc.approval.domain.BaseTimeEntity;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
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
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "toktok_id")
    private Toktok toktok;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment; // 대댓글

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComment;

    @OneToMany(mappedBy = "comment")
    private List<Like> likes;

    @Column(nullable = false)
    private String content; //255자 최대

    @Column(nullable = false)
    private Boolean isDeleted;

    private String imageUrl;

    public void update(String content, String imageUrl) {
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public void deleteWithChildComment() {
        this.content = "[삭제된 댓글입니다.]";
        this.isDeleted = true;
        this.imageUrl = null;
    }
}
