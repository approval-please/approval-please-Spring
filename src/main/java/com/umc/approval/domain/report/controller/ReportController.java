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

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDto.GetReportResponse> getReport(@PathVariable("reportId") Long id) {
        return ResponseEntity.ok(reportService.getReport(id));
    }

    // 결재서류 글 작성시 결재서류 선택 리스트
    @GetMapping("/documents")
    public ResponseEntity<ReportDto.ReportGetDocumentResponse> selectDocument() {
        return ResponseEntity.ok(reportService.selectDocument());
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<Void> updatePost(
            @Valid @RequestBody ReportDto.ReportRequest request,
            @PathVariable("reportId") Long id
    ) {
        reportService.updatePost(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deletePost(@PathVariable("reportId") Long id) {
        reportService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<ReportDto.SearchResponse> search(
            @RequestParam("query") String query,
            @RequestParam("isTag") Integer isTag,
            @RequestParam(value = "category", required = false) Integer category,
            @RequestParam("sortBy") Integer sortBy
    ) {
        return ResponseEntity.ok(reportService.search(query, isTag, category, sortBy));
    }
}
