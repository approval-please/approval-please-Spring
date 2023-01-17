package com.umc.approval.domain.link.service;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.toktok.entity.Toktok;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;

    // 링크 등록
    public void createLink(List<String> links, Document document) {
        for (String link : links) {
            Link newLink = Link.builder().document(document).linkUrl(link).build();
            linkRepository.save(newLink);
        }
    }

    public void createLink(List<String> links, Toktok toktok) {
        for (String link : links) {
            Link newLink = Link.builder().toktok(toktok).linkUrl(link).build();
            linkRepository.save(newLink);
        }
    }
}