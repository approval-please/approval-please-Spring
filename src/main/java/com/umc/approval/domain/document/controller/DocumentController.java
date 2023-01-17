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
@RequestMapping("documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {
    private final DocumentService documentService;
    private final TagService tagService;
    private final LinkService linkService;
    private final ImageService imageService;
    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<?> createDocument(@Valid @RequestPart(value="data", required=false) DocumentRequest.PostDocumentRequest request,
                                            @RequestPart(value="images", required=false) List<MultipartFile> images){

        User user = userService.getUser();

        // 해당하는 카테고리 찾기
        CategoryType categoryType = Arrays.stream(CategoryType.values())
                .filter(c -> c.getValue() == request.getCategory())
                .findAny().get();

        // 게시글 등록
        Document newDocument = documentService.createDocument(request, categoryType, user);

        if(request.getTag() != null)
            tagService.createTag(request.getTag(), newDocument);
        if(request.getLinkUrl() != null)
            linkService.createLink(request.getLinkUrl(), newDocument);
        if(images != null){
            if(images.size()==1) { imageService.createImage(images.get(0), newDocument); }
            else                 { imageService.createImage(images, newDocument);}
        }

        return ResponseEntity.ok().body("게시글 등록 성공!");
    }
}
