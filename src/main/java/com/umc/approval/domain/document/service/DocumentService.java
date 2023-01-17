package com.umc.approval.domain.document.service;

import com.umc.approval.domain.document.dto.DocumentRequest;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.global.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;

    /* 게시글 등록 */
    public Document createDocument(DocumentRequest.PostDocumentRequest req, CategoryType categoryType, User user){
        Document newDocument = Document.builder()
                .user(user)
                .category(categoryType)
                .title(req.getTitle())
                .content(req.getContent())
                .state(2) //승인대기중
                .view(0L)
                .notification(true)
                .build();
        documentRepository.save(newDocument);
        return newDocument;
    }
}
