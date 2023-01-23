package com.umc.approval.domain.profile.controller;

import com.umc.approval.domain.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

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
}