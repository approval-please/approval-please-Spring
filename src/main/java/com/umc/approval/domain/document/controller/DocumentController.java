package com.umc.approval.domain.document.controller;

import com.umc.approval.domain.document.dto.DocumentRequest;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.service.DocumentService;
import com.umc.approval.domain.image.service.ImageService;
import com.umc.approval.domain.link.service.LinkService;
import com.umc.approval.domain.tag.service.TagService;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.service.UserService;
import com.umc.approval.global.type.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<Void> createDocument(@Valid @RequestPart(value="data", required=false) DocumentRequest.PostDocumentRequest request,
                                            @RequestPart(value="images", required=false) List<MultipartFile> images){
        documentService.createDocument(request, images);
        return ResponseEntity.ok().build();
    }
}
