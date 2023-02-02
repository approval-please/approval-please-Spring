package com.umc.approval.domain.notification.controller;

import com.umc.approval.domain.notification.dto.NotificationDto;
import com.umc.approval.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/notification")
@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationDto.UpdateResponse> setNotification(
            @RequestBody NotificationDto.Request request
    ) {
        return ResponseEntity.ok(notificationService.setNotification(request));
    }
}
