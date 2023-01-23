package com.umc.approval.integration.like;

import com.umc.approval.config.AwsS3MockConfig;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.like.service.LikeService;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.RoleType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.umc.approval.global.type.CategoryType.ANIMAL_PLANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Import(AwsS3MockConfig.class)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class LikeServiceIntegrationTest {

    @Autowired
    LikeService likeService;

    @Autowired
    JwtService jwtService;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DocumentRepository documentRepository;

    private User createUser(Long id) {
        return User.builder()
                .email("test" + id + "@test.com")
                .password("test123!")
                .nickname("test" + id)
                .phoneNumber("010-1234-5678")
                .level(0)
                .promotionPoint(0L)
                .build();
    }

    private void loginUser(User user) {
        String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private Document createDocument(User user) {
        Document document = Document.builder()
                .user(user)
                .title("title")
                .content("content")
                .view(0L)
                .category(ANIMAL_PLANT)
                .state(2)
                .notification(true)
                .build();
        return documentRepository.save(document);
    }

    private void follow(User from, User to) {
        Follow follow = Follow.builder()
                .fromUser(from)
                .toUser(to)
                .build();
        followRepository.save(follow);
    }

    private void like(Document document, User user) {
        Like like = Like.builder()
                .user(user)
                .document(document)
                .build();
        likeRepository.save(like);
    }

    @DisplayName("좋아요 목록 조회에 성공한다 - 로그인 X")
    @Test
    void get_like_list_success_unlogin() {

        // given
        User user1 = createUser(1L);
        userRepository.save(user1);
        User user2 = createUser(2L);
        userRepository.save(user2);
        User user3 = createUser(3L);
        userRepository.save(user3);

        Document document = createDocument(user1);

        like(document, user1);
        like(document, user2);
        like(document, user3);

        follow(user1, user2);
        follow(user1, user3);

        LikeDto.ListRequest requestDto = LikeDto.ListRequest.builder()
                .documentId(document.getId())
                .build();

        // when
        LikeDto.ListResponse response = likeService.getLikeList(
                new MockHttpServletRequest(), PageRequest.of(0, 20), requestDto);

        // then
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getTotalPage()).isEqualTo(1);
        assertThat(response.getTotalElement()).isEqualTo(3);
        assertThat(response.getContent().size()).isEqualTo(3);
        assertThat(response.getContent().get(0).getIsFollow()).isFalse();
        assertThat(response.getContent().get(1).getIsFollow()).isFalse();
        assertThat(response.getContent().get(2).getIsFollow()).isFalse();
    }

    @DisplayName("좋아요 목록 조회에 성공한다 - 로그인 O")
    @Test
    void get_like_list_success_login() {

        // given
        User user1 = createUser(1L);
        userRepository.save(user1);
        User user2 = createUser(2L);
        userRepository.save(user2);
        User user3 = createUser(3L);
        userRepository.save(user3);

        Document document = createDocument(user1);

        like(document, user1);
        like(document, user2);
        like(document, user3);

        follow(user1, user2);
        follow(user1, user3);

        LikeDto.ListRequest requestDto = LikeDto.ListRequest.builder()
                .documentId(document.getId())
                .build();

        // 로그인 처리
        String accessToken = jwtService.createAccessToken(user1.getEmail(), user1.getId());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "Bearer " + accessToken);

        // when
        LikeDto.ListResponse response = likeService.getLikeList(
                request, PageRequest.of(0, 20), requestDto);

        // then
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getTotalPage()).isEqualTo(1);
        assertThat(response.getTotalElement()).isEqualTo(3);
        assertThat(response.getContent().size()).isEqualTo(3);
        assertThat(response.getContent().get(0).getIsFollow()).isFalse();
        assertThat(response.getContent().get(1).getIsFollow()).isTrue();
        assertThat(response.getContent().get(2).getIsFollow()).isTrue();
    }
}
