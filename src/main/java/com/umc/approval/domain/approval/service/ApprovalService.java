package com.umc.approval.domain.approval.service;

import com.umc.approval.domain.approval.dto.ApprovalDTO;
import com.umc.approval.domain.approval.entity.Approval;
import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.performance.entity.Performance;
import com.umc.approval.domain.performance.entity.PerformanceRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.PerformanceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.umc.approval.global.exception.CustomErrorType.*;
import static com.umc.approval.global.type.PerformanceType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class ApprovalService {
    private final JwtService jwtService;
    private final ApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final PerformanceRepository performanceRepository;

    public ApprovalDTO.PostOtherApprovalResponse approveOtherDocument(ApprovalDTO.PostOtherApprovalRequest request,
                                                                      Long documentId) {

        User user = certifyUser();
        Document document = findDocument(documentId);

        if (document.getUser().getId() == user.getId()) { // 내 게시글인 경우
            throw new CustomException(CANNOT_APPROVE_MINE);
        } else { // 타 게시글인 경우
            if (document.getState() == 2) { // 승인 대기 중인 경우
                int isApproved = approvalRepository.countByUserIdAndDocumentId(user.getId(), documentId);
                if (isApproved == 0) {
                    Approval approval = request.toEntity(user, document);
                    approvalRepository.save(approval);

                    // 포인트 적립
                    Performance performance = Performance.builder()
                            .user(user)
                            .content(APPROVE_OTHER_DOCUMENT.getContent())
                            .point(APPROVE_OTHER_DOCUMENT.getPoint())
                            .build();
                    performanceRepository.save(performance);
                    user.updatePoint(APPROVE_OTHER_DOCUMENT.getPoint());
                } else {
                    throw new CustomException(APPROVAL_ALREADY_EXISTS);
                }
            } else {
                throw new CustomException(ALREADY_APPROVED);
            }
        }

        int approveCount = approvalRepository.countApproveByDocumentId(documentId);
        int rejectCount = approvalRepository.countRejectByDocumentId(documentId);

        return new ApprovalDTO.PostOtherApprovalResponse(approveCount, rejectCount);
    }

    public void approveMyDocument(ApprovalDTO.PostMyApprovalRequest request) {
        User user = certifyUser();
        Document document = findDocument(request.getDocumentId());

        if (document.getUser().getId() == user.getId()) { // 내 게시글인 경우
            if (document.getState() == 2) { // 승인 대기 중인 경우
                if (request.getIsApprove()) { // 최종 승인 처리
                    documentRepository.updateState(document.getId(), 0);
                } else {
                    documentRepository.updateState(document.getId(), 1);
                }

                // 작성자 실적, 포인트 적립
                Performance performance = Performance.builder()
                        .user(user)
                        .content(FINAL_APPROVE_MY_DOCUMENT.getContent())
                        .point(FINAL_APPROVE_MY_DOCUMENT.getPoint())
                        .build();
                performanceRepository.save(performance);
                user.updatePoint(FINAL_APPROVE_MY_DOCUMENT.getPoint());

                // 결재 참여자 실적, 포인트 적립
                List<User> approveUsers = userRepository.findAllByDocumentIsApprove(document.getId(), request.getIsApprove());
                PerformanceType performanceType = request.getIsApprove() ?
                        FINAL_APPROVE_OTHER_DOCUMENT : FINAL_REJECT_OTHER_DOCUMENT;
                approveUsers.forEach(u -> {
                    Performance p = Performance.builder()
                            .user(u)
                            .content(performanceType.getContent())
                            .point(performanceType.getPoint())
                            .build();
                    performanceRepository.save(p);
                    u.updatePoint(performanceType.getPoint());
                });
            } else {
                throw new CustomException(ALREADY_APPROVED);
            }
        } else { // 타 게시글인 경우
            throw new CustomException(CANNOT_APPROVE_OTHER);
        }
    }

    private User certifyUser() {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        return user;
    }

    private Document findDocument(Long documentId) {
        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isEmpty()) {
            throw new CustomException(DOCUMENT_NOT_FOUND);
        }
        return document.get();
    }
}
