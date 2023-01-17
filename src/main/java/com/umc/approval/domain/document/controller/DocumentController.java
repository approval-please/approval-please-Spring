package com.umc.approval.domain.document.controller;

import com.umc.approval.domain.category.entity.Category;
import com.umc.approval.domain.category.service.CategoryService;
import com.umc.approval.domain.document.dto.DocumentRequest;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.service.DocumentService;
import com.umc.approval.domain.image.service.ImageService;
import com.umc.approval.domain.link.service.LinkService;
import com.umc.approval.domain.tag.service.TagService;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.service.UserService;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {
    private final DocumentService documentService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final LinkService linkService;
    private final ImageService imageService;
    private final UserService userService;

    private final JwtService jwtService;


    /* 게시글 등록 */
    @PostMapping("")
    public ResponseEntity<?> createDocument(@RequestPart(value="data", required=false) @Valid DocumentRequest.PostDocumentRequest request,
                                            @RequestPart(value="image", required = false) MultipartFile image,
                                            @RequestPart(value="images", required=false) List<MultipartFile> images){


        // 임시 사용자(삭제 예정..)
        User temporaryUser = userService.getTemporaryUser().get();

        // 해당하는 카테고리 찾기
        Optional<Category> category = categoryService.getCategory(request.getCategory());

        // 게시글 등록
        Document newDocument = documentService.createDocument(request, category.get(),temporaryUser);

        if(request.getTag() != null)
            tagService.createTag(request.getTag(), newDocument);
        if(request.getLinkUrl() != null)
            linkService.createLink(request.getLinkUrl(), newDocument);
        if(image != null){
            imageService.createImage(image, newDocument);
        }else if(images != null){
            imageService.createImage(images, newDocument);
        }

        return ResponseEntity.ok().body("게시글 등록 성공!");
    }
}
