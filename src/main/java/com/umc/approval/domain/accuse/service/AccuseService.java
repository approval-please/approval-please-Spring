package com.umc.approval.domain.accuse.service;

import com.umc.approval.domain.accuse.dto.AccuseDto;
import com.umc.approval.domain.accuse.entity.Accuse;
import com.umc.approval.domain.accuse.entity.AccuseRepository;
import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class AccuseService {

    private final JwtService jwtService;
    private final AccuseRepository accuseRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final ToktokRepository toktokRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;

    public void accuse(AccuseDto.Request accuseRequest) {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 같은 건에 대한 신고 중복 체크
        accuseDuplicateValidation(user.getId(), accuseRequest);

        // TODO: 본인, 본인 게시글 신고 불가

        // 신고
        Document document = null;
        Toktok toktok = null;
        Report report = null;
        Comment comment = null;
        User accuseUser = null;

        if(accuseRequest.getDocumentId() != null) {
            document = documentRepository.findById(accuseRequest.getDocumentId())
                    .orElseThrow(() -> new CustomException(DOCUMENT_NOT_FOUND));
        } else if(accuseRequest.getToktokId() != null) {
            toktok = toktokRepository.findById(accuseRequest.getToktokId())
                    .orElseThrow(() -> new CustomException(TOKTOKPOST_NOT_FOUND));
        } else if (accuseRequest.getReportId() != null) {
            report = reportRepository.findById(accuseRequest.getReportId())
                    .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));
        } else if (accuseRequest.getCommentId() != null) {
            comment = commentRepository.findById(accuseRequest.getCommentId())
                    .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        } else {
            accuseUser = userRepository.findById(accuseRequest.getAccuseUserId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        }

        Accuse accuse = accuseRequest.toEntity(user, accuseUser, document, toktok, report, comment);
        accuseRepository.save(accuse);
    }

    private void accuseDuplicateValidation(Long userId, AccuseDto.Request accuseRequest) {
        accuseRepository.findByUserAndPost(userId, accuseRequest)
                .ifPresent(accuse -> {
                    throw new CustomException(ACCUSE_ALREADY_EXISTS);
                });
    }
}
