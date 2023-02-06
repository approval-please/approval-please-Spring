package com.umc.approval.domain.profile.service;

import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.dto.FollowDto;
import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.performance.dto.PerformanceDto;
import com.umc.approval.domain.performance.entity.Performance;
import com.umc.approval.domain.performance.entity.PerformanceRepository;
import com.umc.approval.domain.profile.dto.ProfileDto;
import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.scrap.entity.ScrapRepository;
import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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
    private final FollowRepository followRepository;
    private final PerformanceRepository performanceRepository;
    private final CommentRepository commentRepository;
    private final ToktokRepository toktokRepository;
    private final ScrapRepository scrapRepository;
    private final ReportRepository reportRepository;


    // 로그인 확인
    private User certifyUser(Long userId) {
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
    public JSONObject getUserProfile(HttpServletRequest request, Long userId) {
        User user = certifyUser(userId);
        Long loginUserId = jwtService.getIdDirectHeader(request);

        JSONObject result = new JSONObject();
        JSONObject profile = new JSONObject();

        ProfileDto.ProfileResponse response = new ProfileDto.ProfileResponse(
                user.getProfileImage(),
                user.getIntroduction(),
                user.getNickname(),
                user.getLevel(),
                user.getPromotionPoint(),
                followRepository.countByToUser(user.getId()),
                followRepository.countByFromUser(user.getId())
        );

        result.put("content", response);

        if (!((user.getId()).equals(loginUserId))) { // 타 사원증 조회일 경우,
            if (loginUserId != null) { // 로그인 되어 있을 때
                result.put("isFollow", isFollow(loginUserId, user.getId()));
            } else { // 로그인 되어있지 않을 때
                result.put("isFollow", false);
            }
        }

        return result;
    }

    // 결재서류 조회
    public DocumentDto.SearchResponse findDocuments(Long userId, Integer state, Boolean isApproved) {
        User user = certifyUser(userId);
        List<Document> documents = null;

        if (state == null && isApproved == null) { // 전체 조회
            documents = documentRepository.findAllByUserId(user.getId());
        }

        if (state != null) { // 작성한 서류 상태별 조회
            documents = documentRepository.findAllByState(user.getId(), state);
        }

        if (userId == null && isApproved != null) { // 결재한 서류 승인별 조회 (내 사원증만 가능)
            documents = documentRepository.findAllByApproval(user.getId(), isApproved);
        }

        if (userId == null && state != null && isApproved != null) { // 결재한 결재서류 상태별 & 승인별 조회 (내 사원증만 가능)
            documents = documentRepository.findAllByStateApproval(user.getId(), state, isApproved);
        }

        return DocumentDto.SearchResponse.from(documents);
    }

    // 커뮤니티 조회 - 결재톡톡 & 결재보고서
    public Object findCommunity(Long userId, Integer postType) {
        User user = certifyUser(userId);

        if (postType == 1) { // 결재톡톡 (기본값)
            List<Toktok> toktoks = toktokRepository.findByUserId(user.getId());

            if (toktoks.isEmpty()) {
                throw new CustomException(TOKTOKPOST_NOT_FOUND);
            }

            return ToktokDto.ProfileResponse.from(toktoks);

        } else if (postType == 2) { // 결재보고서
            List<Report> reports = reportRepository.findAllByUserId(user.getId());

            if (reports.isEmpty()) {
                throw new CustomException(REPORT_NOT_FOUND);
            }

            return ReportDto.ProfileResponse.from(reports);

        } else {
            throw new CustomException(PARAM_INVALID_VALUE);
        }
    }

    // 댓글 작성한 게시글 조회 (내 사원증만 가능)
    public Object findAllByComments(Integer postType, Integer state) {
        User user = certifyUser(null);

        if (postType == 0) { // 결재서류 (기본값)
            List<Document> documents;

            if (state != null) { // 상태별 조회
                if (state < 0 || state > 2) {
                    throw new CustomException(PARAM_INVALID_VALUE);
                } else {
                    documents = commentRepository.findDocumentsByState(user.getId(), state);
                }

            } else { // 전체 조회
                documents = commentRepository.findDocuments(user.getId());
            }

            return DocumentDto.SearchResponse.from(documents);

        } else if (postType == 1) { // 결재톡톡
            List<Toktok> toktoks = commentRepository.findToktoks(user.getId());

            if (state != null) {
                throw new CustomException(PARAM_INVALID_VALUE);
            }

            return ToktokDto.SearchResponse.from(toktoks);

        } else if (postType == 2) { // 결재보고서
            List<Report> reports = commentRepository.findReports(user.getId());

            if (state != null) {
                throw new CustomException(PARAM_INVALID_VALUE);
            }

            if (reports.isEmpty()) {
                throw new CustomException(POST_WITH_COMMENT_NOT_FOUND);
            }

            return ReportDto.ProfileResponse.from(reports);

        } else {
            throw new CustomException(PARAM_INVALID_VALUE);
        }
    }

    // 스크랩한 게시글 조회 (내 사원증만 가능)
    public Object findAllByScraps(Integer postType, Integer state) {
        User user = certifyUser(null);

        if (postType == 0) { // 결재서류 (기본값)
            List<Document> documents;

            if (state != null) { // 상태별 조회
                if (state < 0 || state > 2) {
                    throw new CustomException(PARAM_INVALID_VALUE);
                } else {
                    documents = scrapRepository.findDocumentsByState(user.getId(), state);
                }

            } else { // 전체 조회
                documents = scrapRepository.findDocuments(user.getId());
            }

            return DocumentDto.SearchResponse.from(documents);

        } else if (postType == 1) { // 결재톡톡
            List<Toktok> toktoks = scrapRepository.findToktoks(user.getId());

            if (state != null) {
                throw new CustomException(PARAM_INVALID_VALUE);
            }

            return ToktokDto.SearchResponse.from(toktoks);

        } else if (postType == 2) { // 결재보고서
            List<Report> reports = scrapRepository.findReports(user.getId());

            if (state != null) {
                throw new CustomException(PARAM_INVALID_VALUE);
            }

            if (reports.isEmpty()) {
                throw new CustomException(POST_WITH_SCRAP_NOT_FOUND);
            }

            return ReportDto.ProfileResponse.from(reports);

        } else {
            throw new CustomException(PARAM_INVALID_VALUE);
        }
    }

    // 실적 조회
    public PerformanceDto.SearchResponse findPerformances() {
        User user = certifyUser(null);
        List<Performance> performances = performanceRepository.findByUserId(user.getId());

        return PerformanceDto.SearchResponse.from(performances);
    }

    // 팔로우 목록 조회
    public JSONObject findMyFollowers() {
        User user = certifyUser(null);
        List<Follow> follows = followRepository.findMyFollowers(user.getId());
        Boolean isFollow;

        JSONObject result = new JSONObject();

        List<FollowDto.FollowListResponse> response = follows.stream()
                .map(follow ->
                        new FollowDto.FollowListResponse(
                                follow.getToUser().getId(),
                                follow.getToUser().getLevel(),
                                follow.getToUser().getNickname(),
                                follow.getToUser().getProfileImage(),
                                isFollow(follow.getToUser().getId(), user.getId())))
                .collect(Collectors.toList());

        result.put("nickname", user.getNickname());
        result.put("totalCount", follows.size());
        result.put("followerCount", followRepository.countByToUser(user.getId()));
        result.put("followingCount", followRepository.countByFromUser(user.getId()));
        result.put("content", response);

        return result;
    }

    // 팔로잉 목록 조회
    public JSONObject findMyFollowings() {
        User user = certifyUser(null);
        List<Follow> followings = followRepository.findMyFollowings(user.getId());

        JSONObject result = new JSONObject();

        List<FollowDto.FollowingListResponse> response = followings.stream()
                .map(following ->
                        new FollowDto.FollowingListResponse(
                                following.getFromUser().getId(),
                                following.getFromUser().getLevel(),
                                following.getFromUser().getNickname(),
                                following.getFromUser().getProfileImage()))
                .collect(Collectors.toList());

        result.put("nickname", user.getNickname());
        result.put("totalCount", followings.size());
        result.put("followerCount", followRepository.countByToUser(user.getId()));
        result.put("followingCount", followRepository.countByFromUser(user.getId()));
        result.put("content", response);

        return result;
    }

    // 사원증 프로필 수정
    public void updateProfile(UserDto.ProfileRequest request) {
        User user = certifyUser(null);

        if (user.getId() == null) {
            throw new CustomException(NO_PERMISSION, "로그인한 사용자만 접근 가능합니다.");
        }

        String nickname = request.getNickname();
        String introduction = request.getIntroduction();
        String image = request.getImage();

        user.update(nickname, introduction, image);
    }

    // 내 팔로잉 사용자가 나를 팔로우 하는지
    public Boolean isFollow(Long fromUserId, Long toUserId) {
        Integer isfollow = followRepository.countFollowOrNot(fromUserId, toUserId);

        if (isfollow == 0) {
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