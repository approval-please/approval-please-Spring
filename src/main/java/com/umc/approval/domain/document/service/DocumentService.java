package com.umc.approval.domain.document.service;

import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.aws.service.AwsS3Service;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class DocumentService {

    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;

    public void createDocument(DocumentDto.DocumentRequest request, List<MultipartFile> images) {
        User user = certifyUser();

        // 해당하는 카테고리 찾기
        CategoryType categoryType = Arrays.stream(CategoryType.values())
                .filter(c -> c.getValue() == request.getCategory())
                .findAny().get();

        // 게시글 등록
        Document document = Document.builder()
                .user(user)
                .category(categoryType)
                .title(request.getTitle())
                .content(request.getContent())
                .state(2) //승인대기중
                .view(0L)
                .notification(true)
                .linkUrl(request.getLinkUrl())
                .build();
        documentRepository.save(document);

        createTag(request, document);
        createImages(images, document);
    }

    public DocumentDto.DocumentResponse getDocument(Long documentId){

        Document document = findDocument(documentId);
        User user = document.getUser();

        List<String> imageUrlList = imageRepository.findImageUrlList(documentId);

        /*
        List<Tag> tagList = tagRepository.findByDocumentId(documentId);
        List<String> tagNameList = new ArrayList<String>();
        for(Tag tag: tagList){
            tagNameList.add(tag.getTag());
        }
        */
        List<String> tagNameList = tagRepository.findTagNameList(documentId);

        DocumentDto.DocumentResponse response = DocumentDto.DocumentResponse.builder()
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .level(user.getLevel())
                .category(document.getCategory().getCategory())
                .title(document.getTitle())
                .content(document.getContent())
                .linkUrl(document.getLinkUrl())
                .tag(tagNameList)
                .images(imageUrlList)
                .state(document.getState())
                .approveCount(0)
                .rejectCount(0)
                .likedCount(0)
                .commentCount(0)
                .modifiedAt(document.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")))
                .view(document.getView())
                .build();

        return response;
    }

    public void updateDocument(Long documentId, DocumentDto.DocumentRequest request, List<MultipartFile> images) {
        // 게시글 존재 유무, 수정 권한 확인
        Document document = findDocument(documentId);
        User user = certifyUser();
        if(user.getId() != document.getUser().getId()){
            throw new CustomException(NO_PERMISSION);
        }

        // document 수정
        CategoryType categoryType = Arrays.stream(CategoryType.values())
                .filter(c -> c.getValue() == request.getCategory())
                .findAny().get();

        document.update(categoryType, request.getTitle(), request.getContent(), request.getLinkUrl());

        // tag 수정
        deleteTag(documentId);
        createTag(request, document);

        // image 수정
        deleteImages(documentId);
        createImages(images, document);
    }


    public void deleteDocument(Long documentId) {
        // 게시글 존재 유무, 삭제 권한 확인
        Document document = findDocument(documentId);
        User user = certifyUser();
        if(user.getId() != document.getUser().getId()){
            throw new CustomException(NO_PERMISSION);
        }

        // tag 삭제
        deleteTag(documentId);

        // image 삭제
        deleteImages(documentId);

        // document 삭제
        documentRepository.deleteById(documentId);
    }


    private User certifyUser(){
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        return user;
    }

    private Document findDocument(Long documentId){
        Optional<Document> document = documentRepository.findById(documentId);
        if(document.isEmpty()){
            throw new CustomException(DOCUMENT_NOT_FOUND);
        }
        return document.get();
    }

    private void createTag(DocumentDto.DocumentRequest request, Document document){
        if (request.getTag() != null) {
            for (String tag : request.getTag()) {
                Tag newTag = Tag.builder().document(document).tag(tag).build();
                tagRepository.save(newTag);
            }
        }
    }

    private void createImages(List<MultipartFile> images, Document document){
        if (images != null) {
            if (images.size() == 1) {
                String imgUrl = awsS3Service.uploadImage(images.get(0));
                Image uploadImg = Image.builder().document(document).imageUrl(imgUrl).build();
                imageRepository.save(uploadImg);
            } else {
                List<String> imgUrls = awsS3Service.uploadImage(images);
                for (String imgUrl : imgUrls) {
                    Image uploadImg = Image.builder().document(document).imageUrl(imgUrl).build();
                    imageRepository.save(uploadImg);
                }
            }
        }
    }

    private void deleteTag(Long documentId){
        List<Tag> tagList = tagRepository.findByDocumentId(documentId);
        if(tagList != null){
            for(Tag tag: tagList){
                tagRepository.deleteById(tag.getId());
            }
        }
    }

    private void deleteImages(Long documentId){
        List<Image> imageList = imageRepository.findByDocumentId(documentId);
        if(imageList != null){
            imageRepository.deleteByDocumentId(documentId);
            for(Image image: imageList){
                awsS3Service.deleteImage(image.getImageUrl());
            }
        }
    }

}
