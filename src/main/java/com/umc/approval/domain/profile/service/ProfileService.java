package com.umc.approval.domain.profile.service;

import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.performance.entity.Performance;
import com.umc.approval.domain.performance.entity.PerformanceRepository;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
@SuppressWarnings("unchecked")
public class ProfileService {
    private final JwtService jwtService;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;
    private final ApprovalRepository approvalRepository;
    private final FollowRepository followRepository;
    private final PerformanceRepository performanceRepository;

    // 사원증 프로필 조회
    public JSONObject getUserProfile (Long userId) {
        User user;

        if (userId == null) { // 내 사원증 조회
            user = certifyUser();
        } else { // 타 사원증 조회
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        }

        JSONObject profile = new JSONObject();

        profile.put("profileImage", user.getProfileImage());
        profile.put("introduction", user.getIntroduction());
        profile.put("nickname", user.getNickname());
        profile.put("level", user.getLevel());
        profile.put("promotionPoint", user.getPromotionPoint());
        profile.put("follows", followRepository.countByToUser(user.getId()));
        profile.put("followings", followRepository.countByFromUser(user.getId()));

        return profile;
    }

    // 결재서류 조회
    public JSONObject findDocuments(Long userId, Integer state, Boolean isApproved) {
        User user;
        List<Document> documents;

        if (userId == null) { // 내 사원증 조회
            user = certifyUser();
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

        if (documents.isEmpty()) {
            throw new CustomException(DOCUMENT_NOT_FOUND);
        }

        JSONObject result = new JSONObject();
        JSONArray documentList = new JSONArray();

        for (int i = 0; i < documents.size(); i++) {
            documentList.add(new DocumentDto.DocumentListResponse(documents.get(i), tagRepository.findTagNameList(documents.get(i).getId()),
                    imageRepository.findImageUrlList(documents.get(i).getId()), approvalRepository.countApproveByDocumentId(documents.get(i).getId()),
                    approvalRepository.countRejectByDocumentId(documents.get(i).getId())));
        }

        result.put("totalCount", documents.size());
        result.put("documentList", documentList);

        return result;
    }

    // 실적 조회
    public JSONObject findPerformances() {
        User user = certifyUser();
        List<Performance> performances;

        performances = performanceRepository.findByUserId(user.getId());

        if(performances.isEmpty()){
            throw new CustomException(PERFORMANCE_NOT_FOUND);
        }

        JSONObject result = new JSONObject();
        JSONArray performanceList = new JSONArray();

        for (int i = 0 ; i < performances.size() ; i++) {
            JSONObject performanceRef = new JSONObject();

            performanceRef.put("date", DateUtil.convert(performances.get(i).getCreatedAt()));
            performanceRef.put("content", performances.get(i).getContent());
            performanceRef.put("point", performances.get(i).getPoint());

            performanceList.add(performanceRef);
        }

        result.put("totalCount", performances.size());
        result.put("performanceList", performanceList);

    // 팔로우 목록 조회
    public JSONObject findMyFollowers () {
        User user = certifyUser();
        List<Follow> follows;

        follows = followRepository.findMyFollowers(user.getId());

        JSONObject result = new JSONObject();
        JSONArray followerList = new JSONArray();

        for (int i = 0 ; i < follows.size() ; i++) {

            JSONObject followRef = new JSONObject();

            followRef.put("level", follows.get(i).getFromUser().getLevel());
            followRef.put("nickname", follows.get(i).getFromUser().getNickname());
            followRef.put("profileImage", follows.get(i).getFromUser().getProfileImage());

            followerList.add(followRef);
        }

        result.put("nickname", user.getNickname());
        result.put("totalCount", follows.size());
        result.put("followerCount", followRepository.countByToUser(user.getId()));
        result.put("followingCount", followRepository.countByFromUser(user.getId()));
        result.put("followerList", followerList);

        return result;
    }

    // 팔로잉 목록 조회
    public JSONObject findMyFollowing () {
        User user = certifyUser();
        List<Follow> follows;

        follows = followRepository.findMyFollowing(user.getId());

        JSONObject result = new JSONObject();
        JSONArray followingList = new JSONArray();

        for (int i = 0 ; i < follows.size() ; i++) {
            JSONObject followRef = new JSONObject();

            followRef.put("level", follows.get(i).getToUser().getLevel());
            followRef.put("nickname", follows.get(i).getToUser().getNickname());
            followRef.put("profileImage", follows.get(i).getToUser().getProfileImage());
            // followRef.put("isFollow", ); // 나를 팔로우 하는지

            followingList.add(followRef);
        }

        result.put("nickname", user.getNickname());
        result.put("totalCount", follows.size());
        result.put("followerCount", followRepository.countByToUser(user.getId()));
        result.put("followingCount", followRepository.countByFromUser(user.getId()));
        result.put("followingList", followingList);

        return result;
    }

    // 사원증 프로필 수정
    public void updateProfile(UserDto.ProfileRequest request) {
        User user = certifyUser();

        String nickname = request.getNickname();
        String introduction = request.getIntroduction();
        String image = request.getImage();

        user.update(nickname, introduction, image);
    }

    // 로그인 확인
    private User certifyUser() {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        return user;
    }
}
