package com.umc.approval.integration.comment;

import com.umc.approval.config.AwsS3MockConfig;
import com.umc.approval.domain.comment.dto.CommentDto;
import com.umc.approval.domain.comment.entity.Comment;
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
import com.umc.approval.global.type.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.umc.approval.global.exception.CustomErrorType.DOCUMENT_NOT_FOUND;
import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;
import static com.umc.approval.global.type.CategoryType.ANIMAL_PLANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(AwsS3MockConfig.class)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class CommentServiceIntegrationTest {

    @Autowired
    CommentService commentService;

    @Autowired
    JwtService jwtService;

    @Autowired
    AwsS3Service awsS3Service;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ToktokRepository toktokRepository;

    @Autowired
    EntityManager em;

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

    private MockMultipartFile createImage() throws IOException {
        String fileName = "test";
        String contentType = "image/png";
        String filePath = "src/test/resources/img/test.png";
        return new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));
    }

    private void loginUser(User user) {
        String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @DisplayName("댓글 등록에 성공한다 - 이미지 X")
    @Test
    void create_comment_success() {

        // given
        User user = createUser(1L);
        userRepository.save(user);
        Document document = Document.builder()
                .user(user)
                .title("title")
                .content("content")
                .view(0L)
                .category(ANIMAL_PLANT)
                .state(2)
                .notification(true)
                .build();
        documentRepository.save(document);
        em.flush();
        em.clear();
        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(document.getId())
                .content("content")
                .build();

        loginUser(user);

        // when
        commentService.createComment(requestDto, null);
        Comment findComment = commentRepository.findAll().get(0);

        // then
        assertThat(findComment.getContent()).isEqualTo(requestDto.getContent());
    }

    @DisplayName("댓글 등록에 성공한다 - 이미지 O")
    @Test
    void create_comment_with_image_success() throws Exception {

        // given
        User user = createUser(1L);
        userRepository.save(user);
        Document document = Document.builder()
                .user(user)
                .title("title")
                .content("content")
                .view(0L)
                .category(ANIMAL_PLANT)
                .state(2)
                .notification(true)
                .build();
        documentRepository.save(document);
        em.flush();
        em.clear();
        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(document.getId())
                .content("content")
                .build();

        loginUser(user);

        // when
        commentService.createComment(requestDto, List.of(createImage()));
        Comment findComment = commentRepository.findAll().get(0);

        // then
        assertThat(findComment.getContent()).isEqualTo(requestDto.getContent());
        assertThat(findComment.getImageUrl()).isNotNull();
    }

    @DisplayName("댓글 등록 시 사용자가 존재하지 않으면 실패한다")
    @Test
    void create_comment_user_not_found_fail() {

        // given
        User user = createUser(1L);
        userRepository.save(user);
        User otherUser = createUser(2L);
        Document document = Document.builder()
                .user(user)
                .title("title")
                .content("content")
                .view(0L)
                .category(ANIMAL_PLANT)
                .state(2)
                .notification(true)
                .build();
        documentRepository.save(document);
        em.flush();
        em.clear();

        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(document.getId())
                .content("content")
                .build();

        String accessToken = jwtService.createAccessToken(otherUser.getEmail(), user.getId() + 20L);
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(otherUser.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

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
        userRepository.save(user);
        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(1L)
                .content("content")
                .build();

        loginUser(user);

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> commentService.createComment(requestDto, null));
        assertThat(e.getErrorType()).isEqualTo(DOCUMENT_NOT_FOUND);
    }
}
