package com.umc.approval.domain.cert.service;

import com.umc.approval.domain.cert.dto.CertDto;
import com.umc.approval.domain.cert.entity.CertRepository;
import com.umc.approval.global.ncloud.sms.SmsService;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CertService {

    private final JwtService jwtService;
    private final SmsService smsService;
    private final CertRepository certRepository;

    public void requestCert(CertDto.CertRequest certRequest) {
        // 전화번호 요청
        smsService.Service();
    }
}
