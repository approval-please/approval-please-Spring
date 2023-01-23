package com.umc.approval.domain.approval.controller;

import com.umc.approval.domain.approval.dto.ApprovalDTO;
import com.umc.approval.domain.approval.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/documents/{documentId}")
    public ResponseEntity<?> approveOtherDocument(@PathVariable("documentId") Long documentId,
                                                  @RequestBody ApprovalDTO.PostOtherApprovalRequest request){
        ApprovalDTO.PostOtherApprovalResponse response = approvalService.approveOtherDocument(request, documentId);
        return ResponseEntity.ok().body(response);
    }

}
