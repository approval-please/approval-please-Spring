package com.umc.approval.domain.comment.service;

import com.umc.approval.domain.comment.dto.CommentDto;
import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class CommentService {

    private final JwtService jwtService;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final ReportRepository reportRepository;
    private final ToktokRepository toktokRepository;
    private final LikeRepository likeRepository;

    public void createComment(CommentDto.CreateRequest requestDto) {

        User user = getUser();

        // 부모 댓글 조회
        Comment parentComment = null;
        if (requestDto.getParentCommentId() != null) {
            parentComment = commentRepository.findById(requestDto.getParentCommentId())
                    .orElseThrow(() -> new CustomException(PARENT_COMMENT_NOT_FOUND));
        }

        // 게시글 조회
        Document document = null;
        Report report = null;
        Toktok toktok = null;

        if (requestDto.getDocumentId() != null) {
            document = documentRepository.findById(requestDto.getDocumentId())
                    .orElseThrow(() -> new CustomException(DOCUMENT_NOT_FOUND));
        } else if (requestDto.getReportId() != null) {
            report = reportRepository.findById(requestDto.getReportId())
                    .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));
        } else {
            toktok = toktokRepository.findById(requestDto.getToktokId())
                    .orElseThrow(() -> new CustomException(TOKTOKPOST_NOT_FOUND));
        }

        commentRepository.save(requestDto.toEntity(user, document, report, toktok, parentComment, requestDto.getImage()));
    }

    public void updateComment(Long commentId, CommentDto.UpdateRequest requestDto) {

        User user = getUser();
        Comment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        // 본인이 쓴 댓글인지 확인
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(NO_PERMISSION);
        }

        comment.update(requestDto.getContent(), requestDto.getImage());
    }

    public void deleteComment(Long commentId) {

        User user = getUser();
        Comment comment = commentRepository.findByIdWithUserAndParentComment(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        // 본인이 쓴 댓글인지 확인
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(NO_PERMISSION);
        }

        // 대댓글 존재 여부에 따른 삭제 처리
        if (commentRepository.existsByParentCommentId(commentId)) {
            comment.deleteWithChildComment();
        } else {
            Comment parentComment = comment.getParentComment();
            commentRepository.delete(comment);
            // 댓글 삭제 후 삭제된 부모 댓글에 대댓글이 더이상 존재하지 않을 경우, 부모 댓글도 db에서 삭제
            if (parentComment != null
                    && !commentRepository.existsByParentCommentId(parentComment.getId())
                    && parentComment.getIsDeleted()) {
                commentRepository.delete(parentComment);
            }
        }
    }

    private User getUser() {
        return userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    public CommentDto.ListResponse getCommentList(HttpServletRequest request, Pageable pageable, CommentDto.Request requestDto) {

        Page<Comment> comments = commentRepository.findAllByPost(pageable, requestDto);
        Integer commentCount = commentRepository.countByPost(requestDto);

        // 글쓴이 조회
        User writer;
        if (requestDto.getDocumentId() != null) {
            Document document = documentRepository.findByIdWithUser(requestDto.getDocumentId())
                    .orElseThrow(() -> new CustomException(DOCUMENT_NOT_FOUND));
            writer = document.getUser();
        } else if (requestDto.getReportId() != null) {
            Report report = reportRepository.findByIdWithUser(requestDto.getReportId())
                    .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));
            writer = report.getDocument().getUser();
        } else {
            Toktok toktok = toktokRepository.findByIdWithUser(requestDto.getToktokId())
                    .orElseThrow(() -> new CustomException(TOKTOKPOST_NOT_FOUND));
            writer = toktok.getUser();
        }

        // (로그인 시) 사용자가 좋아요 누른 댓글 리스트 조회
        Long userId = jwtService.getIdDirectHeader(request);
        List<Comment> allComments = new ArrayList<>(comments.getContent());
        comments.getContent().forEach(c -> {
            if (c.getChildComment() != null) allComments.addAll(c.getChildComment());
        });
        List<Long> commentIds = allComments.stream().map(Comment::getId).collect(Collectors.toList());
        List<Like> likes = likeRepository.findAllByUserAndCommentIn(userId, commentIds);

        return CommentDto.ListResponse.from(comments, commentCount, userId, writer.getId(), likes);
    }
}
