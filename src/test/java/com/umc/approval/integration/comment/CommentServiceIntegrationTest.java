package com.umc.approval.integration.comment;

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
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.umc.approval.global.exception.CustomErrorType.*;
import static com.umc.approval.global.type.CategoryType.ANIMAL_PLANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
public class CommentServiceIntegrationTest {

    @Autowired
    CommentService commentService;

    @Autowired
    JwtService jwtService;

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

    private void loginUser(User user) {
        String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private Comment createComment(User user) {
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
        Comment comment = Comment.builder()
                .user(user)
                .document(document)
                .content("content")
                .isDeleted(false)
                .build();
        return commentRepository.save(comment);
    }

    @DisplayName("댓글 등록에 성공한다")
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
        commentService.createComment(requestDto);
        Comment findComment = commentRepository.findAll().get(0);

        // then
        assertThat(findComment.getContent()).isEqualTo(requestDto.getContent());
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
                () -> commentService.createComment(requestDto));
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
                () -> commentService.createComment(requestDto));
        assertThat(e.getErrorType()).isEqualTo(DOCUMENT_NOT_FOUND);
    }

    @DisplayName("댓글 수정에 성공한다")
    @Test
    void update_comment_success() {

        // given
        User user = createUser(1L);
        Comment comment = createComment(user);
        CommentDto.UpdateRequest requestDto = new CommentDto.UpdateRequest("new content", null);

        loginUser(user);

        // when
        commentService.updateComment(comment.getId(), requestDto);
        Comment findComment = commentRepository.findAll().get(0);

        // then
        assertThat(findComment.getContent()).isEqualTo(requestDto.getContent());
        assertThat(findComment.getImageUrl()).isNotNull();
    }

    @DisplayName("댓글 수정 시 사용자가 존재하지 않으면 실패한다")
    @Test
    void update_comment_not_found_user_fail() {

        // given
        User user = createUser(1L);
        User otherUser = createUser(2L);
        Comment comment = createComment(user);
        CommentDto.UpdateRequest requestDto = new CommentDto.UpdateRequest("new content", null);

        String accessToken = jwtService.createAccessToken(otherUser.getEmail(), user.getId() + 20L);
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(otherUser.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> commentService.updateComment(comment.getId(), requestDto));
        assertThat(e.getErrorType()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("댓글 수정 시 자신의 댓글이 아니면 실패한다")
    @Test
    void update_comment_not_own_comment_fail() throws IOException {

        // given
        User user = createUser(1L);
        User otherUser = createUser(2L);
        userRepository.save(otherUser);
        Comment comment = createComment(user);
        CommentDto.UpdateRequest requestDto = new CommentDto.UpdateRequest("new content", null);

        loginUser(otherUser);

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> commentService.updateComment(comment.getId(), requestDto));
        assertThat(e.getErrorType()).isEqualTo(NO_PERMISSION);
    }

    @DisplayName("댓글 삭제에 성공한다 - 대댓글 X")
    @Test
    void delete_comment_success() {

        // given
        User user = createUser(1L);
        Comment comment = createComment(user);
        loginUser(user);

        // when & then
        List<Comment> beforeResult = commentRepository.findAll();
        assertThat(beforeResult.size()).isEqualTo(1);
        commentService.deleteComment(comment.getId());
        List<Comment> afterResult = commentRepository.findAll();
        assertThat(afterResult).isEmpty();
    }

    @DisplayName("댓글 삭제에 성공한다 - 대댓글 O")
    @Test
    void delete_comment_with_child_comment_success() {

        // given
        User user = createUser(1L);
        Comment comment = createComment(user);
        Document document = comment.getDocument();
        Comment childComment = Comment.builder()
                .user(user)
                .document(document)
                .content("content")
                .parentComment(comment)
                .isDeleted(false)
                .build();
        commentRepository.save(childComment);

        loginUser(user);

        // when
        commentService.deleteComment(comment.getId());
        Comment findComment = commentRepository.findById(comment.getId()).get();

        // then
        assertThat(findComment.getIsDeleted()).isTrue();
        assertThat(findComment.getContent()).isEqualTo("[삭제된 댓글입니다.]");
    }

    @DisplayName("댓글 삭제에 성공한다 - 대댓글 O 이후 대댓글 삭제")
    @Test
    void delete_comment_with_child_comment_delete_success() {

        // given
        User user = createUser(1L);
        Comment comment = createComment(user);
        Document document = comment.getDocument();
        Comment childComment = Comment.builder()
                .user(user)
                .document(document)
                .content("content")
                .parentComment(comment)
                .isDeleted(false)
                .build();
        commentRepository.save(childComment);

        loginUser(user);

        // when & then
        commentService.deleteComment(comment.getId());
        Comment findComment = commentRepository.findById(comment.getId()).get();
        assertThat(findComment.getIsDeleted()).isTrue();
        assertThat(findComment.getContent()).isEqualTo("[삭제된 댓글입니다.]");

        // 대댓글까지 삭제
        commentService.deleteComment(childComment.getId());
        List<Comment> result = commentRepository.findAll();
        assertThat(result).isEmpty();
    }

    @DisplayName("댓글 삭제 시 사용자가 존재하지 않으면 실패한다")
    @Test
    void delete_comment_not_found_user_fail() {

        // given
        User user = createUser(1L);
        User otherUser = createUser(2L);
        Comment comment = createComment(user);

        String accessToken = jwtService.createAccessToken(otherUser.getEmail(), user.getId() + 20L);
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(otherUser.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> commentService.deleteComment(comment.getId()));
        assertThat(e.getErrorType()).isEqualTo(USER_NOT_FOUND);
    }

    @DisplayName("댓글 삭제 시 자신의 댓글이 아니면 실패한다")
    @Test
    void delete_comment_not_own_comment_fail() {

        // given
        User user = createUser(1L);
        User otherUser = createUser(2L);
        userRepository.save(otherUser);
        Comment comment = createComment(user);

        loginUser(otherUser);

        // when & then
        CustomException e = assertThrows(CustomException.class,
                () -> commentService.deleteComment(comment.getId()));
        assertThat(e.getErrorType()).isEqualTo(NO_PERMISSION);
    }
}
