package com.umc.approval.domain.report.controller;

import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.report.service.ReportService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/community/reports")
@RestController
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Void> createPost(@Valid
        @RequestPart(value = "data", required = false) ReportDto.ReportRequest request,
        @RequestPart(value = "images", required = false) List<MultipartFile> files) {

        reportService.createPost(request, files);
        return ResponseEntity.ok().build();

    }

}
