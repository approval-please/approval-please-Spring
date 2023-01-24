package com.umc.approval.domain.profile.controller;

import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.profile.service.ProfileService;
import com.umc.approval.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final ProfileService profileService;

    // 결재서류 조회
    @GetMapping({"/my/documents", "/{userId}/documents", "/my/approvals"})
    public JSONObject findAllDocuments (@PathVariable(value = "userId", required = false) Long userId,
                                        @RequestParam(value = "state", required = false) Integer state,
                                        @RequestParam(value = "isApproved", required = false) Boolean isApproved) {

        return profileService.findDocuments(userId, state, isApproved);
    }

    // 실적 조회
    // @GetMapping("/my/performances")

    // 사원증 프로필 수정
        @PatchMapping("/update")
        public ResponseEntity<Void> updateProfile (@Valid @RequestPart(value = "data", required = false) UserDto.ProfileRequest request,
                @RequestPart(value = "image", required = false) MultipartFile image) {

            profileService.updateProfile(request, image);

        return ResponseEntity.ok().build();
    }
}
