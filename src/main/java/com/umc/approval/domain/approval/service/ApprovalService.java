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

import java.util.Optional;

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
            int isApproved = approvalRepository.countByUserIdAndDocumentId(user.getId(), documentId);
            if(isApproved == 0){
                Approval approval = request.toEntity(user, document);
                approvalRepository.save(approval);
            }else{
                throw new CustomException(APPROVAL_ALREADY_EXISTS);
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
            if(document.getState() == 2){
                if(request.getIsApprove() == true){
                    documentRepository.updateStateApproved(document.getId());
                }else{
                    documentRepository.updateStateRejected(document.getId());
                }
            }else{ // 이미 최종 선택한 경우
                throw new CustomException(CANNOT_CHANGE_APPROVAL);
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
