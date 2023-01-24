package com.umc.approval.domain.document.service;

import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like.entity.LikeRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ApprovalRepository approvalRepository;

    public void createDocument(DocumentDto.DocumentRequest request, List<MultipartFile> images) {
        User user = certifyUser();

        // 해당하는 카테고리 찾기
        CategoryType categoryType = Arrays.stream(CategoryType.values())
                .filter(c -> c.getValue() == request.getCategory())
                .findAny().get();

        // 게시글 등록
        Document document = request.toEntity(user, categoryType);
        documentRepository.save(document);
        createTag(request.getTag(), document);
        createImages(images, document);
    }

    public DocumentDto.GetDocumentResponse getDocument(Long documentId){

        // 조회 수 업데이트
        documentRepository.updateView(documentId);

        // 결재서류 정보
        Document document = findDocument(documentId);
        User user = document.getUser();
        List<String> tagNameList = tagRepository.findTagNameList(documentId);
        List<String> imageUrlList = imageRepository.findImageUrlList(documentId);

        // 승인, 반려 수
        int approveCount = approvalRepository.countApproveByDocumentId(documentId);
        int rejectCount = approvalRepository.countRejectByDocumentId(documentId);

        // 좋아요 수, 댓글 수
        int likedCount = likeRepository.countByDocumentId(documentId);
        int commentCount = commentRepository.countByDocumentId(documentId);

        return new DocumentDto.GetDocumentResponse(document, user, tagNameList, imageUrlList,
                approveCount, rejectCount, likedCount, commentCount);
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
        createTag(request.getTag(), document);

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

    public DocumentDto.GetDocumentListResponse getDocumentList(Integer page, Integer category){
        Pageable pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending()); // 최신순

        Page<Document> documents;
        if(category == null){ // 전체 조회
            documents = documentRepository.findAll(pageable);
        }else{ // 부서별 조회
            if(category<0 || category>17){
                throw new CustomException(INVALID_VALUE, "카테고리는 0부터 17까지의 정수 값입니다.");
            }
            CategoryType categoryType = Arrays.stream(CategoryType.values())
                    .filter(c -> c.getValue() == category)
                    .findAny().get();
            documents = documentRepository.findAllByCategory(categoryType, pageable);
        }

        /*
        List<Document> documentList = documents.getContent();
        for(Document document: documentList){
            List<String> tagList = tagRepository.findTagNameList(document.getId());
            List<String> imageList = imageRepository.findImageUrlList(document.getId());
            int approveCount = approvalRepository.countApproveByDocumentId(document.getId());
            int rejectCount = approvalRepository.countRejectByDocumentId(document.getId());
            new DocumentDto.DocumentListResponse(document, tagList, imageList, approveCount, rejectCount);
        }
        */

        List<DocumentDto.DocumentListResponse> response = documents.getContent().stream()
                .map(document ->
                    new DocumentDto.DocumentListResponse(document,
                            tagRepository.findTagNameList(document.getId()),
                            imageRepository.findImageUrlList(document.getId()),
                            approvalRepository.countApproveByDocumentId(document.getId()),
                            approvalRepository.countRejectByDocumentId(document.getId())))
                .collect(Collectors.toList());

        return new DocumentDto.GetDocumentListResponse(documents, response);
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

    private void createTag(List<String> tags, Document document){
        if (tags != null) {
            for (String tag : tags) {
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
