package com.umc.approval.domain.image.service;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.global.aws.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final AwsS3Service awsS3Service;
    private final ImageRepository imageRepository;
    // 이미지 등록
    public void createImage(List<MultipartFile> image, Document document){
        List<String> imgUrls = awsS3Service.uploadImage(image);
        for(String imgUrl: imgUrls){
            Image uploadImg = Image.builder().document(document).imageUrl(imgUrl).build();
            imageRepository.save(uploadImg);
        }
    }

    public void createImage(MultipartFile image, Document document){
        String imgUrl = awsS3Service.uploadImage(image);
        Image uploadImg = Image.builder().document(document).imageUrl(imgUrl).build();
        imageRepository.save(uploadImg);
    }


}
