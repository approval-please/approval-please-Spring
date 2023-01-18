package com.umc.approval.domain.tag.service;

import com.umc.approval.domain.tag.entity.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

}
