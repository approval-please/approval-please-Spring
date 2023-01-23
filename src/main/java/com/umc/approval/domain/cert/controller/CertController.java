package com.umc.approval.domain.cert.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.umc.approval.domain.cert.dto.CertDto;
import com.umc.approval.domain.cert.service.CertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CertController {

    private final CertService certService;

    @PostMapping("/auth/cert")
    public ResponseEntity<CertDto.SmsResponse> requestCert(
            @RequestBody final CertDto.AuthorizeRequest authorizeRequest
    ) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        CertDto.SmsResponse data = certService.requestCert(authorizeRequest);
        return ResponseEntity.ok().body(data);
    }
}
