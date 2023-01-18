package com.umc.approval.domain.tag.service;

import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.toktok.entity.Toktok;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public void createTag(List<String> tags, Toktok toktok) {
        for (String tag : tags) {
            Tag newTag = Tag.builder().toktok(toktok).tag(tag).build();
            tagRepository.save(newTag);
        }
    }
}
