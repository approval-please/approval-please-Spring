package com.umc.approval.global.aws.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.umc.approval.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.umc.approval.global.exception.CustomErrorType.IMAGE_DELETE_FAILED;
import static com.umc.approval.global.exception.CustomErrorType.IMAGE_UPLOAD_FAILED;

@RequiredArgsConstructor
@Service
public class AwsS3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file) {
        try {
            String fileType = "." + file.getContentType().split("/")[1];
            String randomNum = UUID.randomUUID().toString().substring(0, 6);
            String filename = System.currentTimeMillis() + randomNum + fileType;

            long size = file.getSize();

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(size);
            amazonS3.putObject(
                    new PutObjectRequest(bucket, filename, file.getInputStream(), objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
            return amazonS3.getUrl(bucket, filename).toString();
        } catch (IOException e) {
            throw new CustomException(IMAGE_UPLOAD_FAILED, e);
        }
    }

    public List<String> uploadImage(List<MultipartFile> files) {
        return files.stream().map(this::uploadImage).collect(Collectors.toList());
    }

    public void deleteImage(String path) {
        try {
            List<String> splitList = Arrays.stream(path.split("/")).collect(Collectors.toList());;
            String fileName = splitList.get(splitList.size() - 1);
            amazonS3.deleteObject(bucket, fileName);
        } catch (AmazonServiceException e) {
            throw new CustomException(IMAGE_DELETE_FAILED, e);
        }
    }
}
