package com.umc.approval.unit.like.service;

import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.umc.approval.global.exception.CustomErrorType.DOCUMENT_NOT_FOUND;
import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    LikeService likeService;

    @Mock
    JwtService jwtService;

    @Mock
    LikeRepository likeRepository;

    @Mock
    FollowRepository followRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    DocumentRepository documentRepository;

    @Mock
    ToktokRepository toktokRepository;

    @Mock
    ReportRepository reportRepository;

    @Mock
    CommentRepository commentRepository;

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .email("test" + id + "@test.com")
                .password("test123!" + id)
                .nickname("test" + id)
                .build();
    }

    private Document createDocument() {
        return Document.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();
    }

    @DisplayName("좋아요 추가에 성공한다")
    @Test
    void like_add_success() {

        // given
        User user = createUser(1L);
        Document document = createDocument();
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(documentRepository.findById(any())).willReturn(Optional.of(document));
        given(likeRepository.findByUserAndPost(any(), any())).willReturn(Optional.empty());

        LikeDto.Request requestDto = LikeDto.Request.builder()
                .documentId(document.getId())
                .build();

        // when
        LikeDto.UpdateResponse response = likeService.like(requestDto);

        // then
        assertThat(response.getIsLike()).isTrue();
    }

    @DisplayName("좋아요 취소에 성공한다")
    @Test
    void like_cancel_success() {

        // given
        User user = createUser(1L);
        Document document = createDocument();
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(likeRepository.findByUserAndPost(any(), any())).willReturn(Optional.of(Like.builder().build()));

        LikeDto.Request requestDto = LikeDto.Request.builder()
                .documentId(document.getId())
                .build();

        // when
        LikeDto.UpdateResponse response = likeService.like(requestDto);

        // then
        assertThat(response.getIsLike()).isFalse();
    }

    @DisplayName("좋아요 시 사용자가 존재하지 않으면 실패한다")
    @Test
    void like_user_not_found_fail() {

        // given
        User user = createUser(1L);
        Document document = createDocument();
        given(userRepository.findById(any())).willReturn(Optional.empty());

        LikeDto.Request requestDto = LikeDto.Request.builder()
                .documentId(document.getId())
                .build();

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> likeService.like(requestDto));
        assertThat(e.getErrorType()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("좋아요 시 게시글이 존재하지 않으면 실패한다")
    @Test
    void like_post_not_found_fail() {

        // given
        User user = createUser(1L);
        Document document = createDocument();
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(documentRepository.findById(any())).willReturn(Optional.empty());

        LikeDto.Request requestDto = LikeDto.Request.builder()
                .documentId(document.getId())
                .build();

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> likeService.like(requestDto));
        assertThat(e.getErrorType()).isEqualTo(DOCUMENT_NOT_FOUND);
    }
}
