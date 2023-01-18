package com.umc.approval.domain.document.controller;

import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<Void> createDocument(@Valid @RequestPart(value = "data", required = false) DocumentDto.PostDocumentRequest request,
                                               @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        documentService.createDocument(request, images);
        return ResponseEntity.ok().build();
    }
}
