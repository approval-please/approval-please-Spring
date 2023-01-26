package com.umc.approval.domain.profile.controller;

import com.umc.approval.domain.profile.service.ProfileService;
import com.umc.approval.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final ProfileService profileService;

    // 사원증 프로필 조회
    @GetMapping({"/my", "/{userId}"})
    public JSONObject findProfile(@PathVariable(value = "userId", required = false) Long userId) {
        return profileService.getUserProfile(userId);
    }

    // 결재서류 조회
    @GetMapping({"/my/documents", "/{userId}/documents"})
    public JSONObject findAllDocuments(@PathVariable(value = "userId", required = false) Long userId,
                                       @RequestParam(value = "state", required = false) Integer state,
                                       @RequestParam(value = "isApproved", required = false) Boolean isApproved) {

        return profileService.findDocuments(userId, state, isApproved);
    }

    // 커뮤니티 - 결재톡톡 조회

    // 커뮤니티 - 결재보고서 조회

    // 댓글 조회

    // 스크랩 조회

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
    @GetMapping("/my/following")
    public JSONObject findMyFollowing () {
        return profileService.findMyFollowings();
    }

    // 사원증 프로필 수정
    @PutMapping("/update")
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody UserDto.ProfileRequest request) {
        profileService.updateProfile(request);
        return ResponseEntity.ok().build();
    }
}
