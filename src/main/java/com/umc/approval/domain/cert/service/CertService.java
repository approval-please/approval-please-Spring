package com.umc.approval.domain.cert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.umc.approval.domain.cert.dto.CertDto;
import com.umc.approval.domain.cert.entity.Cert;
import com.umc.approval.domain.cert.entity.CertRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.ncloud.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class CertService {

    private final SmsService smsService;
    private final UserRepository userRepository;
    private final CertRepository certRepository;

    // 인증번호 발송 메서드
    public CertDto.SmsResponse requestCert(CertDto.PhoneRequest phoneRequest)
            throws URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        // 인증번호 생성
        String certNumber = createCertNumber();

        Cert cert = certRepository.findByPhoneNumber(phoneRequest.getPhoneNumber()).orElse(null);

        // 기존 인증요청 있었다면 인증번호만 업데이트
        if(cert != null){
            cert.updateCertNumber(certNumber);
        }
        // 없었다면 전화번호, 인증번호 등록
        else {
            CertDto.CertRequest certRequest = new CertDto.CertRequest(phoneRequest.getPhoneNumber(), certNumber);
            saveCert(certRequest);
        }

        String content = "[결재부탁] 인증번호 [" + certNumber + "]를 입력해주세요.";

        // 인증번호 요청
        return smsService.sendCertSms(new CertDto.MessageDto(phoneRequest.getPhoneNumber(), content));
    }

    // 전화번호 인증 메서드
    public CertDto.CertCheckResponse certCheck(CertDto.CertRequest certRequest) {
        Cert cert = certRepository.findByPhoneNumber(certRequest.getPhoneNumber())
                .orElseThrow(() -> new CustomException(CERT_NOT_FOUND));

        // 인증 시간 5분 초과 체크
        if(LocalDateTime.now().minusMinutes(5).isAfter(cert.getModifiedAt())){
            throw new CustomException(CERT_TIME_OVER);
        }
        System.out.println(cert.getCertNumber() + certRequest.getCertNumber());
        if(cert.getCertNumber().equals(certRequest.getCertNumber())) {
            if(cert.getIsChecked()) {
                // 전화번호 중복 체크
                User user = userRepository.findByPhoneNumber(certRequest.getPhoneNumber()).orElse(null);

                if (user != null) {
                    return CertDto.CertCheckResponse.builder()
                            .isDuplicate(true)
                            .email(maskEmail(user.getEmail()))
                            .socialType(user.getSocialType())
                            .build();
                }
            } else {
                cert.setIsChecked(true);
                return CertDto.CertCheckResponse.builder()
                        .isDuplicate(false)
                        .build();
            }
        }

        throw new CustomException(CERT_FAILED);
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

    // 이메일 마스킹 메서드
    public static String maskEmail(String email) {
        /*
         * 요구되는 메일 포맷
         * {userId}@{domain}
         */
        String regex = "\\b(\\S+)@(\\S+)\\b";
        Matcher matcher = Pattern.compile(regex).matcher(email);
        if (matcher.find()) {
            String id = matcher.group(1);
            /*
             * id의 길이를 기준으로 세글자 초과인 경우 첫 글자 뒤 세자리를 마스킹 처리하고,
             * 두글자, 세글자인 경우 첫 글자 뒤 모든 자리 마스킹,
             * 한글자인 경우 마스킹 처리하지 않음
             *
             * domain 의 경우 기본 3글자 초과이므로 첫 글자 뒤 세자리 마스킹.
             */
            int length = id.length();
            if(length == 1){
                return email.replaceAll("(\\S)@(\\S)[^@]{3}(\\S+)", "$1@$2***$3");
            } else if (length ==2){
                return email.replaceAll("(\\S)[^@]@(\\S)[^@]{3}(\\S+)", "$1*@$2***$3");
            } else if (length == 3) {
                return email.replaceAll("(\\S)[^@]{2}@(\\S)[^@]{3}(\\S+)","$1**@$2***$3");
            } else {
                return email.replaceAll("(\\S)[^@]{3}(\\S*)@(\\S)[^@]{3}(\\S+)","$1***$2@$3***$4");
            }
        }
        return email;
    }
}
