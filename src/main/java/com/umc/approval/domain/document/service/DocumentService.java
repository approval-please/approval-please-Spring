package com.umc.approval.domain.document.service;

import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.like_category.entity.LikeCategory;
import com.umc.approval.domain.like_category.entity.LikeCategoryRepository;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.scrap.entity.Scrap;
import com.umc.approval.domain.scrap.entity.ScrapRepository;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
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
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final LinkRepository linkRepository;
    private final CommentRepository commentRepository;
    private final ApprovalRepository approvalRepository;
    private final LikeCategoryRepository likeCategoryRepository;
    private final ScrapRepository scrapRepository;
    private final ReportRepository reportRepository;

    public void createDocument(DocumentDto.DocumentRequest request) {
        User user = certifyUser();

        CategoryType categoryType = findCategory(request.getCategory());

        // 게시글 등록
        Document document = request.toEntity(user, categoryType);
        documentRepository.save(document);
        createTag(request.getTag(), document);
        if (request.getLink() != null) {
            Link link = Link.builder()
                    .document(document)
                    .url(request.getLink().getUrl())
                    .title(request.getLink().getTitle())
                    .image(request.getLink().getImage())
                    .build();
            linkRepository.save(link);
        }

        createImages(request.getImages(), document);

        // 포인트 적립
        userRepository.updatePoint(user.getId(),100L);
    }

    public DocumentDto.GetDocumentResponse getDocument(HttpServletRequest request, Long documentId) {

        // 조회 수 업데이트
        documentRepository.updateView(documentId);

        // 결재서류 정보
        Document document = findDocument(documentId);
        User user = document.getUser();
        List<String> tagNameList = tagRepository.findTagNameList(documentId);
        List<String> imageUrlList = imageRepository.findImageUrlList(documentId);
        Link link = linkRepository.findByDocumentId(documentId).orElse(null);

        // 승인, 반려 수
        int approveCount = approvalRepository.countApproveByDocumentId(documentId);
        int rejectCount = approvalRepository.countRejectByDocumentId(documentId);

        // 좋아요 수, 댓글 수
        int likedCount = likeRepository.countByDocumentId(documentId);
        int commentCount = commentRepository.countByDocumentId(documentId);

        // 게시글 수정 유무
        boolean isModified = document.getCreatedAt().isEqual(document.getModifiedAt()) ? false : true;

        // 게시글 작성자, 좋아요/스크랩 유무
        Boolean writerOrNot = null;
        boolean likeOrNot = false;
        boolean scrapOrNot = false;
        Long userId = jwtService.getIdDirectHeader(request);

        // 로그인 o
        if(userId != null){
            // 게시글 작성자인지
            if(userId == document.getUser().getId()){
                writerOrNot = true;
            }else{
                writerOrNot = false;
            }

            // 게시글 좋아요/스크랩 유무
            likeOrNot = likeRepository.countByUserAndDocument(user, document) == 0 ? false : true;
            scrapOrNot = scrapRepository.countByUserAndDocument(user, document) == 0 ? false : true;
        }

        // 게시글의 결재보고서가 작성되었는지
        boolean reportMade = false;
        Optional<Report> report = reportRepository.findByDocumentId(documentId);
        if(report != null && !report.isEmpty()){
            reportMade = true;
        }

        return new DocumentDto.GetDocumentResponse(document, user, tagNameList, imageUrlList, link,
                approveCount, rejectCount, likedCount, commentCount, isModified, likeOrNot, scrapOrNot,
                writerOrNot, reportMade);
    }

    public void updateDocument(Long documentId, DocumentDto.DocumentRequest request) {
        // 게시글 존재 유무, 수정 권한 확인
        Document document = findDocument(documentId);
        User user = certifyUser();
        if (user.getId() != document.getUser().getId()) {
            throw new CustomException(NO_PERMISSION);
        }

        // document 수정
        CategoryType categoryType = findCategory(request.getCategory());

        document.update(categoryType, request.getTitle(), request.getContent());

        // tag 수정
        deleteTag(documentId);
        createTag(request.getTag(), document);

        // image 수정
        deleteImages(documentId);
        createImages(request.getImages(), document);

        // link 수정
        linkRepository.findByDocumentId(documentId).ifPresent(linkRepository::delete);
        if (request.getLink() != null) {
            Link link = Link.builder()
                    .document(document)
                    .url(request.getLink().getUrl())
                    .title(request.getLink().getTitle())
                    .image(request.getLink().getImage())
                    .build();
            linkRepository.save(link);
        }
    }


    public void deleteDocument(Long documentId) {
        // 게시글 존재 유무, 삭제 권한 확인
        Document document = findDocument(documentId);
        User user = certifyUser();
        if (user.getId() != document.getUser().getId()) {
            throw new CustomException(NO_PERMISSION);
        }

        // tag 삭제
        deleteTag(documentId);

        // image 삭제
        deleteImages(documentId);

        // link 삭제
        linkRepository.findByDocumentId(documentId).ifPresent(linkRepository::delete);

        // 댓글 삭제
        List<Comment> commentList = commentRepository.findByDocumentId(documentId);
        if (commentList != null) {
            commentRepository.deleteAll(commentList);
        }

        // 좋아요 삭제
        List<Like> likedList = likeRepository.findByDocumentId(documentId);
        if (likedList != null) {
            likeRepository.deleteAll(likedList);
        }

        // 스크랩 삭제
        List<Scrap> scrapList = scrapRepository.findByDocumentId(documentId);
        if (scrapList != null) {
            scrapRepository.deleteAll(scrapList);
        }

        // document 삭제
        documentRepository.deleteById(documentId);
    }

    public DocumentDto.GetDocumentListResponse getDocumentList(Integer category) {
        // 게시글 목록 조회
        List<Document> documents = new ArrayList<>();

        if (category == null) { // 전체 게시글
            documents = documentRepository.findAllWithJoin();
        } else { // 특정 부서에 대한 게시글
            if (category < 0 || category > 17) {
                throw new CustomException(INVALID_VALUE, "카테고리는 0부터 17까지의 정수 값입니다.");
            }
            CategoryType categoryType = findCategory(category);
            documents = documentRepository.findAllByCategory(categoryType);
        }

        List<DocumentDto.DocumentListResponse> response = documents.stream()
                .map(document ->
                        new DocumentDto.DocumentListResponse(
                                document,
                                document.getTags(),
                                document.getImages(),
                                document.getApprovals()))
                .collect(Collectors.toList());

        return new DocumentDto.GetDocumentListResponse(response);
    }

    public DocumentDto.GetDocumentListResponse getLikedDocumentList(Integer category){
        User user = certifyUser();

        // 사용자의 관심부서
        List<CategoryType> likedCategoryList = likeCategoryRepository.findCategoryListByUserId(user.getId());

        // 게시글 목록 조회
        List<Document> documents = new ArrayList<>();

        if(category == null){ // 관심부서 전체 게시글
            documents = documentRepository.findAllByLikedCategory(likedCategoryList);
        }else{ // 관심부서 중 특정 부서에 대한 게시글
            if (category < 0 || category > 17) {
                throw new CustomException(INVALID_VALUE, "카테고리는 0부터 17까지의 정수 값입니다.");
            }
            CategoryType categoryType = findCategory(category);
            if (likedCategoryList.contains(categoryType)){
                documents = documentRepository.findAllByCategory(categoryType);
            }else{
                throw new CustomException(NOT_LIKED_CATEGORY);
            }
        }

        List<DocumentDto.DocumentListResponse> response = documents.stream()
                .map(document ->
                        new DocumentDto.DocumentListResponse(
                                document,
                                document.getTags(),
                                document.getImages(),
                                document.getApprovals()))
                .collect(Collectors.toList());

        return new DocumentDto.GetDocumentListResponse(response);
    }


    private User certifyUser() {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        return user;
    }

    private Document findDocument(Long documentId) {
        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isEmpty()) {
            throw new CustomException(DOCUMENT_NOT_FOUND);
        }
        return document.get();
    }

    private CategoryType findCategory(Integer category) {
        return Arrays.stream(CategoryType.values())
                .filter(c -> c.getValue() == category)
                .findAny().get();
    }

    private void createTag(List<String> tags, Document document) {
        if (tags != null) {
            for (String tag : tags) {
                Tag newTag = Tag.builder().document(document).tag(tag).build();
                tagRepository.save(newTag);
            }
        }
    }

    private void createImages(List<String> images, Document document) {
        if (images != null) {
            for (String imgUrl : images) {
                Image uploadImg = Image.builder().document(document).imageUrl(imgUrl).build();
                imageRepository.save(uploadImg);
            }
        }
    }

    private void deleteTag(Long documentId) {
        List<Tag> tagList = tagRepository.findByDocumentId(documentId);
        if (tagList != null) {
            tagRepository.deleteAll(tagList);
        }
    }

    private void deleteImages(Long documentId) {
        List<Image> imageList = imageRepository.findByDocumentId(documentId);
        if (imageList != null && !imageList.isEmpty()) {
            imageRepository.deleteAll(imageList);
        }
    }

    @Transactional(readOnly = true)
    public DocumentDto.SearchResponse search(String query, Integer isTag, Integer category, Integer state, Integer sortBy) {
        List<Document> documents = documentRepository.findAllByQuery(query, isTag, category, state, sortBy);
        return DocumentDto.SearchResponse.from(documents);
    }
}
