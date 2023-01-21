package com.umc.approval.domain.comment.service;

import com.umc.approval.domain.comment.dto.CommentDto;
import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.aws.service.AwsS3Service;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class CommentService {

    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final ReportRepository reportRepository;
    private final ToktokRepository toktokRepository;

    public void createComment(CommentDto.CreateRequest requestDto, List<MultipartFile> images) {

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

        // 이미지
        String imageUrl = null;
        if (images != null && !images.isEmpty()) {
            imageUrl = awsS3Service.uploadImage(images.get(0));
        }

        commentRepository.save(requestDto.toEntity(user, document, report, toktok, parentComment, imageUrl));
    }

    public void updateComment(Long commentId, CommentDto.UpdateRequest requestDto, List<MultipartFile> images) {

        User user = getUser();
        Comment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        // 본인이 쓴 댓글인지 확인
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(NO_PERMISSION);
        }

        // 기존 이미지 존재 시 삭제
        if (comment.getImageUrl() != null) {
            awsS3Service.deleteImage(comment.getImageUrl());
        }

        // 변경된 이미지 추가
        String imageUrl = null;
        if (images != null && !images.isEmpty()) {
            imageUrl = awsS3Service.uploadImage(images.get(0));
        }
        comment.update(requestDto.getContent(), imageUrl);
    }

    public void deleteComment(Long commentId) {

        User user = getUser();
        Comment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        // 본인이 쓴 댓글인지 확인
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(NO_PERMISSION);
        }
    }

    private User getUser() {
        return userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}
