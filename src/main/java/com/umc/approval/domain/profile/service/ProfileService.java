package com.umc.approval.domain.profile.service;

import com.umc.approval.domain.comment.entity.Comment;
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
import com.umc.approval.domain.scrap.entity.Scrap;
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


    // ????????? ??????
    private User certifyUser(Long userId) {
        User user;

        if (userId == null) { // ??? ????????? ??????
            user = userRepository.findById(jwtService.getId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        } else { // ??? ????????? ??????
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        }

        return user;
    }

    // ????????? ????????? ??????
    public JSONObject getUserProfile(HttpServletRequest request, Long userId) {
        User user = certifyUser(userId);
        Long loginUserId = jwtService.getIdDirectHeader(request);

        JSONObject result = new JSONObject();
        JSONObject profile = new JSONObject();

        ProfileDto.ProfileResponse response = new ProfileDto.ProfileResponse(
                userId == null ? loginUserId : userId,
                user.getProfileImage(),
                user.getIntroduction(),
                user.getNickname(),
                user.getLevel(),
                user.getPromotionPoint(),
                followRepository.countByToUser(user.getId()),
                followRepository.countByFromUser(user.getId())
        );

        result.put("content", response);

        // ??? ????????? ??????
        if (userId != null) {
            // ????????? ?????? ???
            if (loginUserId == null) {
                result.put("isFollow", false);
                result.put("isMy", false);
            } else {
                // ???????????? ??? - ??? ?????????
                result.put("isFollow", isFollow(loginUserId, user.getId()));
                result.put("isMy", userId.equals(loginUserId));
            }
        }
        return result;
    }

    // ???????????? ??????
    public DocumentDto.SearchResponse findDocuments(Long userId, Integer state, Boolean isApproved) {
        User user = certifyUser(userId);
        List<Document> documents = null;

        if (state == null && isApproved == null) { // ?????? ??????
            documents = documentRepository.findAllByUserId(user.getId());
        }

        if (state != null) { // ????????? ?????? ????????? ??????
            documents = documentRepository.findAllByState(user.getId(), state);
        }

        if (userId == null && isApproved != null) { // ????????? ?????? ????????? ?????? (??? ???????????? ??????)
            documents = documentRepository.findAllByApproval(user.getId(), isApproved);
        }

        if (userId == null && state != null && isApproved != null) { // ????????? ???????????? ????????? & ????????? ?????? (??? ???????????? ??????)
            documents = documentRepository.findAllByStateApproval(user.getId(), state, isApproved);
        }

        return DocumentDto.SearchResponse.from(documents);
    }

    // ???????????? ?????? - ???????????? & ???????????????
    public Object findCommunity(Long userId, Integer postType) {
        User user = certifyUser(userId);

        if (postType == null) { // ???????????? (?????????)
            List<Toktok> toktoks = toktokRepository.findByUserId(user.getId());
            return ToktokDto.ProfileResponse.from(toktoks);

        } else if (postType == 0) { // ???????????????
            List<Report> reports = reportRepository.findAllByUserId(user.getId());
            return ReportDto.ProfileResponse.from(reports);

        } else {
            throw new CustomException(PARAM_INVALID_VALUE);
        }
    }

    // ?????? ????????? ????????? ?????? (??? ???????????? ??????)
    public Object findAllByComments(Integer postType, Integer state) {
        User user = certifyUser(null);

        if (postType == null) { // ???????????? (?????????)
            List<Document> documents;

            if (state != null) { // ????????? ??????
                if (state < 0 || state > 2) {
                    throw new CustomException(PARAM_INVALID_VALUE);
                } else {
                    documents = commentRepository.findCommentByUserAndDocumentAndState(user.getId(), state)
                            .stream().map(Comment::getDocument).distinct().collect(Collectors.toList());
                }

            } else { // ?????? ??????
                documents = commentRepository.findCommentByUserAndDocument(user.getId())
                        .stream().map(Comment::getDocument).distinct().collect(Collectors.toList());
            }

            return DocumentDto.ProfileResponse.from(documents);

        } else if (postType == 0) { // ????????????
            List<Toktok> toktoks = commentRepository.findCommentsByUserAndToktok(user.getId())
                    .stream().map(Comment::getToktok).distinct().collect(Collectors.toList());

            if (state != null) {
                throw new CustomException(PARAM_INVALID_VALUE);
            }

            return ToktokDto.ProfileResponse.from(toktoks);

        } else if (postType == 1) { // ???????????????
            List<Report> reports = commentRepository.findCommentsByUserAndReport(user.getId())
                    .stream().map(Comment::getReport).distinct().collect(Collectors.toList());

            if (state != null) {
                throw new CustomException(PARAM_INVALID_VALUE);
            }

            return ReportDto.ProfileResponse.from(reports);

        } else {
            throw new CustomException(PARAM_INVALID_VALUE);
        }
    }

    // ???????????? ????????? ?????? (??? ???????????? ??????)
    public Object findAllByScraps(Integer postType, Integer state) {
        User user = certifyUser(null);

        if (postType == null) { // ???????????? (?????????)
            List<Document> documents;

            if (state != null) { // ????????? ??????
                if (state < 0 || state > 2) {
                    throw new CustomException(PARAM_INVALID_VALUE);
                } else {
                    documents = scrapRepository.findScrapsByUserAndDocumentAndState(user.getId(), state)
                            .stream().map(Scrap::getDocument).collect(Collectors.toList());
                }

            } else { // ?????? ??????
                documents = scrapRepository.findScrapsByUserAndDocument(user.getId())
                        .stream().map(Scrap::getDocument).collect(Collectors.toList());
            }

            return DocumentDto.ProfileResponse.from(documents);

        } else if (postType == 0) { // ????????????
            List<Toktok> toktoks = scrapRepository.findScrapsByUserAndToktok(user.getId())
                    .stream().map(Scrap::getToktok).collect(Collectors.toList());

            if (state != null) {
                throw new CustomException(PARAM_INVALID_VALUE);
            }

            return ToktokDto.ProfileResponse.from(toktoks);

        } else if (postType == 1) { // ???????????????
            List<Report> reports = scrapRepository.findScrapsByUserAndReport(user.getId())
                    .stream().map(Scrap::getReport).collect(Collectors.toList());

            if (state != null) {
                throw new CustomException(PARAM_INVALID_VALUE);
            }

            return ReportDto.ProfileResponse.from(reports);

        } else {
            throw new CustomException(PARAM_INVALID_VALUE);
        }
    }

    // ?????? ??????
    public PerformanceDto.SearchResponse findPerformances() {
        User user = certifyUser(null);
        List<Performance> performances = performanceRepository.findByUserId(user.getId());

        return PerformanceDto.SearchResponse.from(performances);
    }

    // ????????? ?????? ??????
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

    // ????????? ?????? ??????
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

    // ????????? ????????? ??????
    public void updateProfile(UserDto.ProfileRequest request) {
        User user = certifyUser(null);

        if (user.getId() == null) {
            throw new CustomException(NO_PERMISSION, "???????????? ???????????? ?????? ???????????????.");
        }

        String nickname = request.getNickname();
        String introduction = request.getIntroduction();
        String image = request.getImage();

        user.updateProfile(nickname, introduction, image);
    }

    // ??? ????????? ???????????? ?????? ????????? ?????????
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