package com.umc.approval.integration.like;

import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.like.service.LikeService;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.umc.approval.global.exception.CustomErrorType.DOCUMENT_NOT_FOUND;
import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;
import static com.umc.approval.global.type.CategoryType.ANIMAL_PLANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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

    @Autowired
    ToktokRepository toktokRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    CommentRepository commentRepository;

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

        // when
        LikeDto.ListResponse response = likeService.getLikeList(
                new MockHttpServletRequest(), document.getId(), null, null);

        // then
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

        // 로그인 처리
        String accessToken = jwtService.createAccessToken(user1.getEmail(), user1.getId());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "Bearer " + accessToken);

        // when
        LikeDto.ListResponse response = likeService.getLikeList(request, document.getId(), null, null);

        // then
        assertThat(response.getContent().size()).isEqualTo(3);
        assertThat(response.getContent().get(0).getIsFollow()).isFalse();
        assertThat(response.getContent().get(1).getIsFollow()).isTrue();
        assertThat(response.getContent().get(2).getIsFollow()).isTrue();
    }

    @DisplayName("좋아요 추가/취소에 성공한다")
    @Test
    void like_add_success() {

        // given
        User user = createUser(1L);
        userRepository.save(user);
        Document document = createDocument(user);
        LikeDto.Request requestDto = LikeDto.Request.builder()
                .documentId(document.getId())
                .build();

        loginUser(user);

        // when & then
        LikeDto.UpdateResponse response = likeService.like(requestDto);
        assertThat(response.getIsLike()).isTrue();
        response = likeService.like(requestDto);
        assertThat(response.getIsLike()).isFalse();
    }

    @DisplayName("좋아요 시 사용자가 존재하지 않으면 실패한다")
    @Test
    void like_user_not_found_fail() {

        // given
        User user = createUser(1L);
        userRepository.save(user);
        User otherUser = createUser(2L);
        Document document = createDocument(user);
        LikeDto.Request requestDto = LikeDto.Request.builder()
                .documentId(document.getId())
                .build();

        String accessToken = jwtService.createAccessToken(otherUser.getEmail(), user.getId() + 20L);
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(otherUser.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> likeService.like(requestDto));
        assertThat(e.getErrorType()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("좋아요 시 게시글이 존재하지 않으면 실패한다")
    @Test
    void like_post_not_found_fail() {

        // given
        User user = createUser(1L);
        userRepository.save(user);
        Document document = createDocument(user);
        LikeDto.Request requestDto = LikeDto.Request.builder()
                .documentId(document.getId() + 20L)
                .build();

        loginUser(user);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> likeService.like(requestDto));
        assertThat(e.getErrorType()).isEqualTo(DOCUMENT_NOT_FOUND);
    }
}
