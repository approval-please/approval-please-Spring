package com.umc.approval.domain.profile.controller;

import com.umc.approval.domain.profile.dto.ProfileDto;
import com.umc.approval.domain.profile.service.ProfileService;
import com.umc.approval.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final ProfileService profileService;


    // 사원증 프로필 조회
    @GetMapping({"/my", "/{userId}"})
    public JSONObject findProfile(HttpServletRequest request,
                                  @PathVariable(value = "userId", required = false) Long userId) {
        return profileService.getUserProfile(request, userId);
    }

    // 결재서류 조회
    @GetMapping({"/my/documents", "/{userId}/documents"})
    public JSONObject findDocuments(@PathVariable(value = "userId", required = false) Long userId,
                                    @RequestParam(value = "state", required = false) Integer state,
                                    @RequestParam(value = "isApproved", required = false) Boolean isApproved) {
        return profileService.findDocuments(userId, state, isApproved);
    }

    /*
    // 커뮤니티 - 결재톡톡 조회
    @GetMapping({"/my/community/toktoks", "/{userId}/community/toktoks"})
    public JSONObject findToktoks(@PathVariable(value = "userId", required = false) Long userId) {
        return profileService.findToktoks(userId);
    }

    // 커뮤니티 - 결재보고서 조회
    @GetMapping({"/my/community/reports", "/{userId}/community/reports"})
    public ResponseEntity<ReportDto.GetReportResponse> findReports(@PathVariable(value = "userId", required = false) Long userId) {
        return ResponseEntity.ok(profileService.findReports(userId));
    }
     */

    // 댓글 작성한 게시글 조회
    @GetMapping("/my/comments")
    public JSONObject findAllByComments(@RequestParam(value = "postType", required = false) Integer postType,
                                        @RequestParam(value = "state", required = false) Integer state) {
        return profileService.findAllByComments(postType, state);
    }

    // 스크랩한 게시글 조회
    @GetMapping("/my/scraps")
    public JSONObject findAllByScraps(@RequestParam(value = "postType", required = false) Integer postType,
                                      @RequestParam(value = "state", required = false) Integer state) {
        return profileService.findAllByScraps(postType, state);
    }

    // 실적 조회
    @GetMapping("/my/performances")
    public JSONObject findPerformances () {
        return profileService.findPerformances();
    }

    // 팔로우 조회
    @GetMapping("/my/followers")
    public JSONObject findMyFollowers () {
        return profileService.findMyFollowers();
    }

    // 팔로잉 조회
    @GetMapping("/my/followings")
    public JSONObject findMyFollowings () {
        return profileService.findMyFollowings();
    }

    // 사원증 프로필 수정
    @PutMapping("/update")
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody UserDto.ProfileRequest request) {
        profileService.updateProfile(request);
        return ResponseEntity.ok().build();
    }

    // 사원 검색
    @GetMapping("/search")
    public ResponseEntity<ProfileDto.SearchResponse> search(@RequestParam("query") String query) {
        return ResponseEntity.ok(profileService.search(query));
    }
}
