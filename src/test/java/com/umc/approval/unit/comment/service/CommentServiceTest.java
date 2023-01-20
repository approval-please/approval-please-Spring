package com.umc.approval.unit.comment.service;

import com.umc.approval.domain.comment.dto.CommentDto;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.comment.service.CommentService;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.aws.service.AwsS3Service;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.umc.approval.global.exception.CustomErrorType.DOCUMENT_NOT_FOUND;
import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    JwtService jwtService;

    @Mock
    AwsS3Service awsS3Service;

    @Mock
    CommentRepository commentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    DocumentRepository documentRepository;

    @Mock
    ReportRepository reportRepository;

    @Mock
    ToktokRepository toktokRepository;

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .email("test" + id + "@test.com")
                .password("test123!" + id)
                .nickname("test" + id)
                .build();
    }

    private MockMultipartFile createImage() throws IOException {
        String fileName = "test";
        String contentType = "image/png";
        String filePath = "src/test/resources/img/test.png";
        return new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));
    }

    @DisplayName("댓글 등록에 성공한다 - 이미지 X")
    @Test
    void create_comment_success() {

        // given
        User user = createUser(1L);
        Document document = Document.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(documentRepository.findById(any())).willReturn(Optional.of(document));
        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(1L)
                .build();

        // when & then
        commentService.createComment(requestDto, null);
    }

    @DisplayName("댓글 등록에 성공한다 - 이미지 O")
    @Test
    void create_comment_with_image_success() throws Exception {

        // given
        User user = createUser(1L);
        Document document = Document.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(documentRepository.findById(any())).willReturn(Optional.of(document));
        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(1L)
                .build();

        MockMultipartFile image = createImage();

        // when & then
        commentService.createComment(requestDto, List.of(image));
    }

    @DisplayName("댓글 등록 시 사용자가 존재하지 않으면 실패한다")
    @Test
    void create_comment_user_not_found_fail() {

        // given
        given(userRepository.findById(any())).willReturn(Optional.empty());
        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(1L)
                .build();

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> commentService.createComment(requestDto, null));
        assertThat(e.getErrorType()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("댓글 등록 시 게시글(결재서류)이 존재하지 않으면 실패한다")
    @Test
    void create_comment_post_not_found_fail() {

        // given
        User user = createUser(1L);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(documentRepository.findById(any())).willReturn(Optional.empty());
        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(1L)
                .build();

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> commentService.createComment(requestDto, null));
        assertThat(e.getErrorType()).isEqualTo(DOCUMENT_NOT_FOUND);
    }
}
