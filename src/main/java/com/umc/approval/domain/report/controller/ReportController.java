package com.umc.approval.domain.report.controller;

import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.report.service.ReportService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDto.GetReportResponse> getReport(@PathVariable("reportId") Long id) {
        return ResponseEntity.ok(reportService.getReport(id));
    }

    // 결재서류 글 작성시 결재서류 선택 리스트
    @GetMapping("/documents")
    public ResponseEntity<ReportDto.ReportGetDocumentResponse> selectDocument(@RequestParam("page") Integer page) {
        return ResponseEntity.ok(reportService.selectDocument(page));
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<Void> updatePost(@Valid
        @RequestPart(value = "data", required = false) ReportDto.ReportRequest request,
        @RequestPart(value = "images", required = false) List<MultipartFile> files,
        @PathVariable("reportId") Long id) {

        reportService.updatePost(id, request, files);
        return ResponseEntity.ok().build();
    }
}
