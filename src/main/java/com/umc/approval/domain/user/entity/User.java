package com.umc.approval.domain.user.entity;


import com.umc.approval.domain.BaseTimeEntity;
import com.umc.approval.global.type.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String profileImage;

    @Enumerated(value = STRING)
    private SocialType socialType;

    private Long socialId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private String introduction;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Long promotionPoint;

    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void deleteRefreshToken() {
        this.refreshToken = null;
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 사원증 프로필 수정
    public void update(String nickname, String introduction) {
        this.nickname = nickname;
        this.introduction = introduction;
    }
}
