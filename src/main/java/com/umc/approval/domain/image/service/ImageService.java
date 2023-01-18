package com.umc.approval.domain.image.service;

import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.global.aws.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final AwsS3Service awsS3Service;
    private final ImageRepository imageRepository;

}
