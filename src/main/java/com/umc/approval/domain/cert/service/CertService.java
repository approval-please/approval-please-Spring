package com.umc.approval.domain.cert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.umc.approval.domain.cert.dto.CertDto;
import com.umc.approval.domain.cert.entity.Cert;
import com.umc.approval.domain.cert.entity.CertRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.ncloud.sms.service.SmsService;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class CertService {

    private final JwtService jwtService;
    private final SmsService smsService;
    private final UserRepository userRepository;
    private final CertRepository certRepository;

    // 인증번호 발송 메서드
    public CertDto.SmsResponse requestCert(CertDto.PhoneRequest phoneRequest)
            throws URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        // 인증번호 생성
        String certNumber = createCertNumber();
        // 전화번호, 인증번호 등록
        CertDto.CertRequest certRequest = new CertDto.CertRequest(phoneRequest.getPhoneNumber(), certNumber);
        saveCert(certRequest);

        // 인증 문자 내용
        String content = "[결재부탁] 인증번호 [" + certNumber + "]를 입력해주세요.";

        // 인증번호 요청
        return smsService.sendCertSms(new CertDto.MessageDto(phoneRequest.getPhoneNumber(), content));
    }

    // 전화번호 인증 메서드
    public CertDto.CertCheckResponse certCheck(CertDto.CertRequest certRequest) {
        //TODO: 5분 이내에 입력했는지 체크, 코드 리팩토링

        //전화번호 중복 체크 - userRepository
        Optional<User> user = userRepository.findByPhoneNumber(certRequest.getPhoneNumber());
        if (user.isPresent()){
            return CertDto.CertCheckResponse.builder()
                    .isDuplicate(true)
                    .email(maskEmail(user.get().getEmail()))
                    .socialType(user.get().getSocialType())
                    .build();
        }

        Cert cert = certRepository.findByPhoneNumber(certRequest.getPhoneNumber())
                .orElseThrow(() -> new CustomException(CERT_NOT_FOUND));

        if(cert.getCertNumber().equals(certRequest.getCertNumber())) {
            certRepository.deleteByPhoneNumber(certRequest.getPhoneNumber());
            return CertDto.CertCheckResponse.builder()
                    .isDuplicate(false)
                    .build();
        }
        certRepository.deleteByPhoneNumber(certRequest.getPhoneNumber());
        throw new CustomException(CERT_NUMBER_NOT_EQUAL);
    }

    // 인증번호 생성 메서드
    private String createCertNumber() {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        int count = 6;
        StringBuilder certNumber = new StringBuilder();

        for (int i=0; i<count; i++) {
            certNumber.append(random.nextInt(9));
        }

        return certNumber.toString();
    }

    // 전화번호, 인증번호 등록 메서드
    private void saveCert(CertDto.CertRequest certRequest) {
        Cert cert = certRequest.toEntity();
        certRepository.save(cert);
    }

    // TODO: 이메일 가리기 메서드 간결하게 수정
    // 이메일 일부 가리기 메서드
    private String maskEmail(String email) {
        String[] splitEmail = email.split("@");

        StringBuilder maskedEmailId = new StringBuilder();
        StringBuilder maskedEmailDomain = new StringBuilder();

        maskedEmailId
                .append(splitEmail[0])
                .replace(1, 4, "***")
                .append("@");

        maskedEmailDomain
                .append(splitEmail[1])
                .replace(1, 4, "***");

        return maskedEmailId + maskedEmailDomain.toString();
    }
}
