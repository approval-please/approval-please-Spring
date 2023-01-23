package com.umc.approval.domain.cert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.umc.approval.domain.cert.dto.CertDto;
import com.umc.approval.domain.cert.entity.Cert;
import com.umc.approval.domain.cert.entity.CertRepository;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.ncloud.sms.service.SmsService;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Transactional
@RequiredArgsConstructor
@Service
public class CertService {

    private final JwtService jwtService;
    private final SmsService smsService;
    private final UserRepository userRepository;
    private final CertRepository certRepository;

    // 난수 생성 메서드
    private String createCertNumber() {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        int count = 6;
        StringBuilder certNumber = new StringBuilder();

        for (int i=0; i<count; i++) {
            certNumber.append(Integer.toString(random.nextInt(9)));
        }

        return certNumber.toString();
    }

    // 인증번호 발송 메서드
    public CertDto.SmsResponse requestCert(CertDto.AuthorizeRequest authorizeRequest)
            throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String certNumber = createCertNumber();
        CertDto.CertRequest certRequest = new CertDto.CertRequest(authorizeRequest.getPhoneNumber(), certNumber);
        saveCert(certRequest);

        // 인증 문자 내용
        String content = "[결재부탁] 인증번호 [" + certNumber + "]를 입력해주세요.";

        // 인증번호 요청
        return smsService.sendCertSms(new CertDto.MessageDto(authorizeRequest.getPhoneNumber(), content));
    }

    private void saveCert(CertDto.CertRequest certRequest) {
        Cert cert = certRequest.toEntity();
        certRepository.save(cert);
    }
}
