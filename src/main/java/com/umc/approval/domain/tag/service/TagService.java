package com.umc.approval.domain.tag.service;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.tag.entity.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public void createTag(List<String> tags, Document document){
        for(String tag: tags){
            Tag newTag = Tag.builder().document(document).tag(tag).build();
            tagRepository.save(newTag);
        }
    }
}
