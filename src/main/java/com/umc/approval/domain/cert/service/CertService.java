package com.umc.approval.domain.cert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.umc.approval.domain.cert.dto.CertDto;
import com.umc.approval.domain.cert.entity.CertRepository;
import com.umc.approval.global.ncloud.sms.service.SmsService;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Transactional
@RequiredArgsConstructor
@Service
public class CertService {

    private final JwtService jwtService;
    private final SmsService smsService;
    private final CertRepository certRepository;

    public CertDto.CertSmsResponse requestCert(CertDto.MessageDto messageDto) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        // 인증번호 요청
        return smsService.sendCertSms(messageDto);
    }

    // TODO: 아래 테스트용 코드 삭제
    public void test(){
        smsService.testService();
    }
}
