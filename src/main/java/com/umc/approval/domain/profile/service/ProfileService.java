package com.umc.approval.domain.profile.service;

import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.umc.approval.global.exception.CustomErrorType.DOCUMENT_NOT_FOUND;
import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;

@Transactional
@RequiredArgsConstructor
@Service
public class ProfileService {
    private final JwtService jwtService;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;
    private final ApprovalRepository approvalRepository;
    private final FollowRepository followRepository;

    // 마이페이지 - 결재서류 조회
    public JSONObject findDocuments (Long userId, Integer state, Boolean isApproved) {
        User user;
        List<Document> documents;

        if (userId == null) { // 내 사원증 조회
            user = userRepository.findById(jwtService.getId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        } else { // 타 사원증 조회
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        }

        if (state != null) { // 작성한 서류 상태별 조회
            documents = documentRepository.findAllByState(user.getId(), state);
        } else if (isApproved != null) { // 결재한 서류 승인별 조회
            documents = documentRepository.findAllByApproval(user.getId(), isApproved);
        } else { // 전체 조회
            documents = documentRepository.findAllByUserId(user.getId());
        }

        if(documents.isEmpty()){
            throw new CustomException(DOCUMENT_NOT_FOUND);
        }

        JSONObject result = new JSONObject();
        JSONObject profile = new JSONObject();
        JSONArray documentList = new JSONArray();

        for (int i = 0 ; i < documents.size() ; i++) {
            documentList.add(new DocumentDto.DocumentListResponse(documents.get(i), tagRepository.findTagNameList(documents.get(i).getId()), imageRepository.findImageUrlList(documents.get(i).getId()),
                        approvalRepository.countApproveByDocumentId(documents.get(i).getId()), approvalRepository.countRejectByDocumentId(documents.get(i).getId())));
        }

        profile.put("profileImage", user.getProfileImage());
        profile.put("nickname", user.getNickname());
        profile.put("level", user.getLevel());
        profile.put("promotionPoint", user.getPromotionPoint());
        profile.put("follows", followRepository.countByToUser(user.getId()));
        profile.put("followings", followRepository.countByFromUser(user.getId()));

        result.put("totalCount", documents.size());
        result.put("profile", profile);
        result.put("documentList", documentList);

        return result;
    }
}
