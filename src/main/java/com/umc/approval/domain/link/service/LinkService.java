package com.umc.approval.domain.link.service;

import com.umc.approval.domain.link.entity.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;


}