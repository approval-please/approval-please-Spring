package com.umc.approval.domain.report.controller;

import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/community/reports")
@RestController
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody ReportDto.ReportRequest request) {
        reportService.createPost(request);
        return ResponseEntity.ok().build();
    }

    // 결재서류 글 작성시 결재서류 선택 리스트
    @GetMapping("/documents")
    public ResponseEntity<ReportDto.ReportGetDocumentResponse> selectDocument(@RequestParam("page") Integer page) {
        return ResponseEntity.ok(reportService.selectDocument(page));
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<Void> updatePost(
            @Valid @RequestBody ReportDto.ReportRequest request,
            @PathVariable("reportId") Long id
    ) {
        reportService.updatePost(id, request);
        return ResponseEntity.ok().build();
    }
}
