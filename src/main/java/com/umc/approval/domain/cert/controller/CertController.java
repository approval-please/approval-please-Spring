package com.umc.approval.domain.cert.controller;

import com.umc.approval.domain.cert.dto.CertDto;
import com.umc.approval.domain.cert.service.CertService;
import com.umc.approval.domain.comment.dto.CommentDto;
import com.umc.approval.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CertController {

    private final CertService certService;

    @PostMapping("/auth/cert")
    public ResponseEntity<Void> requestCert(
            @RequestBody final CertDto.CertRequest certRequest
    ) {
        certService.requestCert(certRequest);
        return ResponseEntity.ok().build();
    }

}
