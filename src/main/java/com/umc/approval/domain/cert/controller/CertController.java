package com.umc.approval.domain.cert.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.umc.approval.domain.cert.dto.CertDto;
import com.umc.approval.domain.cert.service.CertService;
import com.umc.approval.domain.comment.dto.CommentDto;
import com.umc.approval.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CertController {

    private final CertService certService;

    @PostMapping("/auth/cert")
    public ResponseEntity<CertDto.CertSmsResponse> requestCert(
            @RequestBody final CertDto.MessageDto messageDto
    ) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        CertDto.CertSmsResponse data = certService.requestCert(messageDto);
        return ResponseEntity.ok().body(data);
    }

    // TODO: 아래 테스트용 코드 삭제
    @PostMapping("/auth/certTest")
    public ResponseEntity<Void> test(
            @RequestBody final CertDto.MessageDto messageDto
    ) {
        certService.test();
        return ResponseEntity.ok().build();
    }

}
