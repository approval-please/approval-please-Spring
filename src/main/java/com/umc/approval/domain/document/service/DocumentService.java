package com.umc.approval.domain.document.service;

import com.umc.approval.domain.category.entity.Category;
import com.umc.approval.domain.document.dto.DocumentRequest;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    public Document createDocument(DocumentRequest.BasicWorkRequest req, Category category, User user){
        Document newDocument = Document.builder()
                .user(user)
                .category(category)
                .title(req.getTitle())
                .content(req.getContent())
                .state(2) //승인대기중
                .view(0L)
                .notification(true) //넣을 필요x?
                .build();
        documentRepository.save(newDocument);
        return newDocument;
    }
}
