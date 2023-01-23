package com.umc.approval.global.ncloud.sms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.cert.dto.CertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@PropertySource("classpath:application-local.yml")
public class SmsService {

    @Value("${ncloud.sms.accessKey}")
    private String accessKey;

    @Value("${ncloud.sms.secretKey}")
    private String secretKey;

    @Value("${ncloud.sms.serviceId}")
    private String serviceId;

    @Value("${ncloud.sms.senderPhone}")
    private String fromPhoneNumber;

    public CertDto.SmsResponse sendCertSms(CertDto.MessageDto messageDto)
            throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Long time = System.currentTimeMillis();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("x-ncp-apigw-timestamp", time.toString());
        httpHeaders.set("x-ncp-iam-access-key", this.accessKey);
        httpHeaders.set("x-ncp-apigw-signature-v2", makeSignature(time));

        List<CertDto.MessageDto> messages = new ArrayList<>();
        messages.add(messageDto);

        CertDto.SmsRequest certSmsRequest = CertDto.SmsRequest.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(this.fromPhoneNumber)
                .content(messageDto.getContent())
                .messages(messages)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String httpBody = objectMapper.writeValueAsString(certSmsRequest);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpBody, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        return restTemplate.postForObject(
                new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"),
                httpEntity, CertDto.SmsResponse.class);
    }

    public String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + this.serviceId + "/messages";
        String timestamp = time.toString();
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message =
                method +
                space +
                url +
                newLine +
                timestamp +
                newLine +
                accessKey;

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(rawHmac);
    }
}
