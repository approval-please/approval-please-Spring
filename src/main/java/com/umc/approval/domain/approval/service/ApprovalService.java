package com.umc.approval.domain.approval.service;

import com.umc.approval.domain.approval.dto.ApprovalDTO;
import com.umc.approval.domain.approval.entity.Approval;
import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class ApprovalService {
    private final JwtService jwtService;
    private final ApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public ApprovalDTO.PostOtherApprovalResponse approveOtherDocument(ApprovalDTO.PostOtherApprovalRequest request,
                                                                      Long documentId) {

        User user = certifyUser();
        Document document = findDocument(documentId);

        if(document.getUser().getId() == user.getId()){ // 내 게시글인 경우
            throw new CustomException(CANNOT_APPROVE_MINE);
        }else{ // 타 게시글인 경우
            if(document.getState() == 2){ // 승인 대기 중인 경우
                int isApproved = approvalRepository.countByUserIdAndDocumentId(user.getId(), documentId);
                if(isApproved == 0){
                    Approval approval = request.toEntity(user, document);
                    approvalRepository.save(approval);

                    // 포인트 적립
                    userRepository.updatePoint(user.getId(), 50L);
                }else{
                    throw new CustomException(APPROVAL_ALREADY_EXISTS);
                }
            }else{
                throw new CustomException(ALREADY_APPROVED);
            }
        }

        int approveCount = approvalRepository.countApproveByDocumentId(documentId);
        int rejectCount = approvalRepository.countRejectByDocumentId(documentId);

        return new ApprovalDTO.PostOtherApprovalResponse(approveCount, rejectCount);
    }

    public void approveMyDocument(ApprovalDTO.PostMyApprovalRequest request){
        User user = certifyUser();
        Document document = findDocument(request.getDocumentId());

        if(document.getUser().getId() == user.getId()){ // 내 게시글인 경우
            if(document.getState() == 2){ // 승인 대기 중인 경우
                if(request.getIsApprove() == true){ // 최종 승인 처리
                    documentRepository.updateState(document.getId(), 0);
                }else{
                    documentRepository.updateState(document.getId(), 1);
                }

                // 작성자 포인트 적립
                userRepository.updatePoint(user.getId(), 100L);
                // 결재 참여자 포인트 적립
                List<Long> userIdList = approvalRepository.findByDocumentIdAndIsApprove(document.getId(), request.getIsApprove());
                userRepository.updatePoint(userIdList, 100L);
            }else{
                throw new CustomException(ALREADY_APPROVED);
            }
        }else{ // 타 게시글인 경우
            throw new CustomException(CANNOT_APPROVE_OTHER);
        }
    }

    private User certifyUser(){
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        return user;
    }

    private Document findDocument(Long documentId){
        Optional<Document> document = documentRepository.findById(documentId);
        if(document.isEmpty()){
            throw new CustomException(DOCUMENT_NOT_FOUND);
        }
        return document.get();
    }
}
