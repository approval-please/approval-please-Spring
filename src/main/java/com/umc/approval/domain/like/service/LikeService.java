package com.umc.approval.domain.like.service;

import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.like.dto.LikeDto;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class LikeService {

    private final JwtService jwtService;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final ToktokRepository toktokRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public LikeDto.ListResponse getLikeList(HttpServletRequest request, Long documentId, Long toktokId, Long reportId) {

        List<Like> likes = likeRepository.findAllByPost(documentId, toktokId, reportId);

        // 팔로우 처리
        Long userId = jwtService.getIdDirectHeader(request);
        List<LikeDto.Response> response;
        if (userId != null) {
            // 로그인 O
            List<Long> userIds = likes.stream().map(l -> l.getUser().getId()).collect(Collectors.toList());
            List<Follow> follows = followRepository.findAllByToUserId(userId, userIds);

            response = likes.stream()
                    .map(l -> {
                        Boolean isFollow = follows.stream().anyMatch(f ->
                                f.getToUser().getId() == l.getUser().getId());
                        return LikeDto.Response.fromEntity(l, isFollow, userId);
                    }).collect(Collectors.toList());
        } else {
            // 로그인 X
            response = likes.stream()
                    .map(l -> LikeDto.Response.fromEntity(l, false, null))
                    .collect(Collectors.toList());
        }
        return LikeDto.ListResponse.from(response);
    }

    public LikeDto.UpdateResponse like(LikeDto.Request requestDto) {

        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Optional<Like> findLike = likeRepository.findByUserAndPost(user.getId(), requestDto);

        // 좋아요 추가가 되어있는 경우 -> 취소
        if (findLike.isPresent()) {
            likeRepository.delete(findLike.get());
            return new LikeDto.UpdateResponse(false);
        }

        // else 좋아요 추가
        Document document = null;
        Toktok toktok = null;
        Report report = null;
        Comment comment = null;

        if (requestDto.getDocumentId() != null) {
            document = documentRepository.findById(requestDto.getDocumentId())
                    .orElseThrow(() -> new CustomException(DOCUMENT_NOT_FOUND));
        } else if (requestDto.getToktokId() != null) {
            toktok = toktokRepository.findById(requestDto.getToktokId())
                    .orElseThrow(() -> new CustomException(TOKTOKPOST_NOT_FOUND));
        } else if (requestDto.getReportId() != null) {
            report = reportRepository.findById(requestDto.getReportId())
                    .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));
        } else {
            comment = commentRepository.findById(requestDto.getCommentId())
                    .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        }
        Like like = Like.builder()
                .user(user)
                .document(document)
                .toktok(toktok)
                .report(report)
                .comment(comment)
                .build();
        likeRepository.save(like);
        return new LikeDto.UpdateResponse(true);
    }
}
