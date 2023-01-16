package com.umc.approval.domain.document.controller;

import com.umc.approval.domain.category.entity.Category;
import com.umc.approval.domain.category.service.CategoryService;
import com.umc.approval.domain.document.dto.DocumentRequest;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.service.DocumentService;
import com.umc.approval.domain.link.service.LinkService;
import com.umc.approval.domain.tag.service.TagService;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final LinkService linkService;
    private final UserService userService;

    /* 게시글 등록 */
    @PostMapping("")
    public ResponseEntity<?> createDocument(@RequestBody DocumentRequest.BasicWorkRequest req){

        // 필수값(category, title, content) null 예외처리


        // 임시 user
        User temporaryUser = userService.getTemporaryUser().get();

        // 해당하는 카테고리 찾기
        Optional<Category> category = categoryService.getCategory(req.getCategory());

        // 게시글 등록
        Document newDocument = documentService.createDocument(req, category.get(),temporaryUser);
        //imageService.createImage(req, newDocument);
        tagService.createTag(req.getTag(), newDocument);
        linkService.createLink(req.getLinkUrl(), newDocument);

        return ResponseEntity.ok().body("게시글 등록 성공!");
    }
}
