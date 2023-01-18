package com.umc.approval.domain.link.service;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.toktok.entity.Toktok;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;

    public void createLink(List<String> links, Toktok toktok) {
        for (String link : links) {
            Link newLink = Link.builder().toktok(toktok).linkUrl(link).build();
            linkRepository.save(newLink);
        }
    }
}