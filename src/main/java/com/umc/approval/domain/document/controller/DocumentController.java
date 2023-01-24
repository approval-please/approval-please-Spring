package com.umc.approval.domain.document.controller;

import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<Void> createDocument(@Valid @RequestPart(value = "data", required = false) DocumentDto.DocumentRequest request,
                                               @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        documentService.createDocument(request, images);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentDto.GetDocumentResponse> getDocument(@PathVariable("documentId") Long documentId){
        return ResponseEntity.ok().body(documentService.getDocument(documentId));
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<Void> updateDocument(@PathVariable("documentId") Long documentId,
                                               @Valid @RequestPart(value = "data", required = false) DocumentDto.DocumentRequest request,
                                               @RequestPart(value = "images", required = false) List<MultipartFile> images){
        documentService.updateDocument(documentId, request, images);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable("documentId") Long documentId){
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<DocumentDto.GetDocumentListResponse> getDocumentList(
                                                        @RequestParam("page") Integer page,
                                                        @RequestParam(required = false) Integer category){
        return ResponseEntity.ok().body(documentService.getDocumentList(page, category));
    }
}
