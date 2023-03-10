package com.umc.approval.global.ncloud.sms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.cert.dto.CertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
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
@PropertySource(value = {"classpath:application-local.yml", "classpath:application.yml"}, ignoreResourceNotFound = true)
public class SmsService {

    @Value("${accessKey:none}")
    private String accessKey;

    @Value("${secretKey:none}")
    private String secretKey;

    @Value("${serviceId:none}")
    private String serviceId;

    @Value("${senderPhone:none}")
    private String fromPhoneNumber;

    public CertDto.SmsResponse sendCertSms(CertDto.MessageDto messageDto)
            throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException {
        Long time = System.currentTimeMillis();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("x-ncp-apigw-timestamp", time.toString());
        httpHeaders.set("x-ncp-iam-access-key", this.accessKey);
        httpHeaders.set("x-ncp-apigw-signature-v2", makeSignature(time));

        List<CertDto.MessageDto> messages = new ArrayList<>();
        messages.add(messageDto);

        CertDto.SmsRequest smsRequest = CertDto.SmsRequest.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(this.fromPhoneNumber)
                .content(messageDto.getContent())
                .messages(messages)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String httpBody = objectMapper.writeValueAsString(smsRequest);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpBody, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        return restTemplate.postForObject(
                new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"),
                httpEntity, CertDto.SmsResponse.class);
    }

    private String makeSignature(Long time) throws NoSuchAlgorithmException, InvalidKeyException {
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
