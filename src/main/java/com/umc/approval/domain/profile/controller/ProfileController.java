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

    // 결재서류 조회
    @GetMapping({"/my/documents", "/{userId}/documents", "/my/approvals"})
    public JSONObject findAllDocuments(@PathVariable(value = "userId", required = false) Long userId,
                                       @RequestParam(value = "state", required = false) Integer state,
                                       @RequestParam(value = "isApproved", required = false) Boolean isApproved) {

        return profileService.findDocuments(userId, state, isApproved);
    }

    // 실적 조회
    // @GetMapping("/my/performances")

    // 사원증 프로필 수정
    @PutMapping("/update")
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody UserDto.ProfileRequest request) {
        profileService.updateProfile(request);
        return ResponseEntity.ok().build();
    }
}
