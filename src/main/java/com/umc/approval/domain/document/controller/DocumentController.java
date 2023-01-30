package com.umc.approval.domain.document.controller;

import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<Void> createDocument(@Valid @RequestBody DocumentDto.DocumentRequest request) {
        documentService.createDocument(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentDto.GetDocumentResponse> getDocument(
            HttpServletRequest request,
            @PathVariable("documentId") Long documentId) {
        return ResponseEntity.ok().body(documentService.getDocument(request, documentId));
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<Void> updateDocument(
        @PathVariable("documentId") Long documentId,
        @Valid @RequestBody DocumentDto.DocumentRequest request
    ) {
        documentService.updateDocument(documentId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable("documentId") Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<DocumentDto.GetDocumentListResponse> getDocumentList(
            @RequestParam(value = "category", required = false) Integer category,
            @RequestParam(value = "state", required = false) Integer state,
            @RequestParam(value = "sortBy", required = false) Integer sortBy) {
        return ResponseEntity.ok().body(documentService.getDocumentList(category, state, sortBy));
    }

    @GetMapping("/likes")
    public ResponseEntity<DocumentDto.GetDocumentListResponse> getLikedLDocumentList(
            @RequestParam(value = "category", required = false) Integer category,
            @RequestParam(value = "state", required = false) Integer state,
            @RequestParam(value = "sortBy", required = false) Integer sortBy){
        return ResponseEntity.ok().body(documentService.getLikedDocumentList(category, state, sortBy));
    }

    @GetMapping("/search")
    public ResponseEntity<DocumentDto.SearchResponse> search(
        @RequestParam("query") String query,
        @RequestParam("isTag") Integer isTag,
        @RequestParam(value = "category", required = false) Integer category,
        @RequestParam(value = "state", required = false) Integer state,
        @RequestParam("sortBy") Integer sortBy
    ) {
        return ResponseEntity.ok(documentService.search(query, isTag, category, state, sortBy));
    }
}