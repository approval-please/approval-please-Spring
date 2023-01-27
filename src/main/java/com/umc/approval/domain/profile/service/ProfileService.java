package com.umc.approval.domain.profile.service;

import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.dto.FollowDto;
import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.performance.dto.PerformanceDto;
import com.umc.approval.domain.performance.entity.Performance;
import com.umc.approval.domain.performance.entity.PerformanceRepository;
import com.umc.approval.domain.profile.dto.ProfileDto;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    // 로그인 확인
    private User certifyUser (Long userId){
        User user;

        if (userId == null) { // 내 사원증 조회
            user = userRepository.findById(jwtService.getId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        } else { // 타 사원증 조회
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        }

        return user;
    }

    // 사원증 프로필 조회
    public JSONObject getUserProfile (Long userId) {
        User user = certifyUser(userId);

        JSONObject result = new JSONObject();
        JSONObject profile = new JSONObject();

        profile.put("profileImage", user.getProfileImage());
        profile.put("introduction", user.getIntroduction());
        profile.put("nickname", user.getNickname());
        profile.put("level", user.getLevel());
        profile.put("promotionPoint", user.getPromotionPoint());
        profile.put("follows", followRepository.countByToUser(user.getId()));
        profile.put("followings", followRepository.countByFromUser(user.getId()));

        result.put("content", profile);

        return result;
    }

    // 결재서류 조회
    public JSONObject findDocuments(Long userId, Integer state, Boolean isApproved) {
        User user = certifyUser(userId);
        List<Document> documents = null;

        if (userId == null) { // 타 사원증 조회 x
            if (isApproved != null) { // 결재한 서류 승인별 조회
                documents = documentRepository.findAllByApproval(user.getId(), isApproved);
            }

            if (state != null && isApproved != null) { // 결재한 결재서류 상태별 & 승인별 조회 (타 사원증 조회 x)
                documents = documentRepository.findAllByStateApproval(user.getId(), state, isApproved);
            }
        }
            if (state != null) { // 작성한 서류 상태별 조회
                documents = documentRepository.findAllByState(user.getId(), state);
            }
            if (state == null && isApproved == null) { // 전체 조회
                documents = documentRepository.findAllByUserId(user.getId());
            }

            if (documents.isEmpty()) {
                throw new CustomException(DOCUMENT_NOT_FOUND);
            }

            JSONObject result = new JSONObject();

            List<DocumentDto.DocumentListResponse> response = documents.stream()
                    .map(document ->
                            new DocumentDto.DocumentListResponse(
                                    document,
                                    document.getTags(),
                                    document.getImages(),
                                    document.getApprovals()))
                    .collect(Collectors.toList());

            result.put("totalElement", documents.size());
            result.put("content", response);

            return result;
        }

        /*
        // 커뮤니티 - 결재톡톡 조회
        public JSONObject findToktoks (Long userId){
            User user = certifyUser(userId);
            List<Toktok> toktoks;
        }

        // 커뮤니티 - 결재보고서 조회
        public JSONObject findReports (Long userId){
            User user = certifyUser(userId);
            List<Report> reports;
        }
         */

        // 실적 조회
        public JSONObject findPerformances () {
            User user = certifyUser(null);
            List<Performance> performances;

            performances = performanceRepository.findByUserId(user.getId());

            if (performances.isEmpty()) {
                throw new CustomException(PERFORMANCE_NOT_FOUND);
            }

            JSONObject result = new JSONObject();

            List<PerformanceDto.PerformanceListResponse> response = performances.stream()
                    .map(performance ->
                            new PerformanceDto.PerformanceListResponse(
                                    performance,
                                    performance.getContent(),
                                    performance.getPoint()))
                    .collect(Collectors.toList());

            result.put("totalElement", performances.size());
            result.put("content", response);

            return result;
        }

        // 팔로우 목록 조회
        public JSONObject findMyFollowers () {
            User user = certifyUser(null);
            List<Follow> follows;

            follows = followRepository.findMyFollowers(user.getId());

            if (follows.isEmpty()) {
                throw new CustomException(FOLLOW_NOT_FOUND);
            }

            JSONObject result = new JSONObject();

            List<FollowDto.FollowListResponse> response = follows.stream()
                    .map(follow ->
                            new FollowDto.FollowListResponse(
                                    follow.getFromUser().getLevel(),
                                    follow.getFromUser().getNickname(),
                                    follow.getFromUser().getProfileImage()))
                    .collect(Collectors.toList());

            result.put("myNickname", user.getNickname());
            result.put("totalElement", follows.size());
            result.put("followerCount", followRepository.countByToUser(user.getId()));
            result.put("followingCount", followRepository.countByFromUser(user.getId()));
            result.put("content", response);

            return result;
        }

        // 팔로잉 목록 조회
        public JSONObject findMyFollowings () {
            User user = certifyUser(null);
            List<Follow> followings;
            Boolean isFollow;

            followings = followRepository.findMyFollowings(user.getId());

            if (followings.isEmpty()) {
                throw new CustomException(FOLLOWING_NOT_FOUND);
            }

            JSONObject result = new JSONObject();

            List<FollowDto.FollowingListResponse> response = followings.stream()
                    .map(following ->
                            new FollowDto.FollowingListResponse(
                                    following.getToUser().getLevel(),
                                    following.getToUser().getNickname(),
                                    following.getToUser().getProfileImage(),
                                    isFollow(following.getToUser().getId(), user.getId())))
                    .collect(Collectors.toList());

            result.put("nickname", user.getNickname());
            result.put("totalElement", followings.size());
            result.put("followerCount", followRepository.countByToUser(user.getId()));
            result.put("followingCount", followRepository.countByFromUser(user.getId()));
            result.put("content", response);

            return result;
        }

        // 사원증 프로필 수정
        public void updateProfile (UserDto.ProfileRequest request){
            User user = certifyUser(null);

            String nickname = request.getNickname();
            String introduction = request.getIntroduction();
            String image = request.getImage();

            user.update(nickname, introduction, image);
        }

        // 내 팔로잉 사용자가 나를 팔로우 하는지
        public Boolean isFollow (Long fromUserId, Long toUserId) {
            Integer isfollow = followRepository.countFollowOrNot(fromUserId, toUserId);

            if(isfollow == 0) {
                return false;
            } else {
                return true;
            }
        }

    public ProfileDto.SearchResponse search(String query) {
        List<User> users = userRepository.findByNicknameContains(query);
        return ProfileDto.SearchResponse.from(users);
    }
}