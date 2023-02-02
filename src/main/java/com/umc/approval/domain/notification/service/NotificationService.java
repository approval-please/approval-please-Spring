package com.umc.approval.domain.notification.service;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.notification.dto.NotificationDto;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final DocumentRepository documentRepository;
    private final ToktokRepository toktokRepository;
    private final ReportRepository reportRepository;

    public NotificationDto.UpdateResponse setNotification(NotificationDto.Request request) {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (request.getDocumentId() != null) {
            Document document = documentRepository.findById(request.getDocumentId())
                    .orElseThrow(() -> new CustomException(DOCUMENT_NOT_FOUND));
            document.setNotification(!document.getNotification());
            return new NotificationDto.UpdateResponse(document.getNotification());
        } else if(request.getToktokId() != null) {
            Toktok toktok = toktokRepository.findById(request.getToktokId())
                    .orElseThrow(() -> new CustomException(TOKTOKPOST_NOT_FOUND));
            toktok.setNotification(!toktok.isNotification());
            return new NotificationDto.UpdateResponse(toktok.isNotification());
        } else {
            Report report = reportRepository.findById(request.getReportId())
                    .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));
            report.setNotification(!report.getNotification());
            return new NotificationDto.UpdateResponse(report.getNotification());
        }
    }

}
