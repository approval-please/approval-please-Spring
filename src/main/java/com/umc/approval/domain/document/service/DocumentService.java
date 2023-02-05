package com.umc.approval.domain.document.service;

import com.umc.approval.domain.accuse.entity.Accuse;
import com.umc.approval.domain.accuse.entity.AccuseRepository;
import com.umc.approval.domain.approval.entity.Approval;
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
import com.umc.approval.domain.report.service.ReportService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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
    private final ReportService reportService;
    private final AccuseRepository accuseRepository;

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
        userRepository.updatePoint(user.getId(), 100L);
    }

    public DocumentDto.GetDocumentResponse getDocument(HttpServletRequest request, Long documentId) {

        // 조회 수 업데이트
        documentRepository.updateView(documentId);

        // 결재서류 정보
        Document document = findDocument(documentId);
        User writer = document.getUser(); // 결재서류 작성자
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
        boolean isWriter = false;
        boolean isLiked = false;
        boolean isScrap = false;
        int isVoted = 0;
        Long userId = jwtService.getIdDirectHeader(request); // 로그인 한 사용자

        // 로그인 o
        if (userId != null) {
            User user = userRepository.findById(userId).get();

            // 게시글 작성자인지
            if(userId == writer.getId()){
                isWriter = true;
                if (document.getState() == 2) {
                    isVoted = 0;
                } else if (document.getState() == 1) {
                    isVoted = 2;
                } else {
                    isVoted = 1;
                }
            }else{
                isWriter = false;

                // 로그인한 사용자의 타 게시글에 대한 승인/반려 여부
                Optional<Approval> approval = approvalRepository.findByUserAndDocument(user, document);
                if(approval != null && !approval.isEmpty()){ // 승인/반려 선택한 경우
                    if(approval.get().getIsApprove() == true)
                        isVoted = 1;
                    else // 반려
                        isVoted = 2;
                }
            }

            // 게시글 좋아요/스크랩 유무
            isLiked = likeRepository.countByUserAndDocument(user, document) == 0 ? false : true;
            isScrap = scrapRepository.countByUserAndDocument(user, document) == 0 ? false : true;
        }

        // 게시글의 결재보고서가 작성되었는지
        boolean reportMade = false;
        Long reportId = null;
        Optional<Report> report = reportRepository.findByDocumentId(documentId);
        if (report != null && !report.isEmpty()) {
            reportMade = true;
            reportId = report.get().getId();
        }

        return new DocumentDto.GetDocumentResponse(document, writer, tagNameList, imageUrlList, link,
                approveCount, rejectCount, likedCount, commentCount, isModified, isLiked, isScrap,
                isWriter, reportMade, reportId, isVoted);
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

        // 승인/반려 삭제
        List<Approval> approvalList = approvalRepository.findByDocumentId(documentId);
        if(approvalList != null){
            approvalRepository.deleteAll(approvalList);
        }

        // 결재보고서 및 관련 내용 모두 삭제
        reportRepository.findByDocumentId(documentId).ifPresent(r -> reportService.deletePost(r.getId()));

        // 신고 내역 삭제
        List<Accuse> accuseList = accuseRepository.findByDocumentId(documentId);
        if(accuseList != null){
            accuseRepository.deleteAll(accuseList);
        }

        // document 삭제
        documentRepository.deleteById(documentId);
    }

    @Transactional(readOnly = true)
    public DocumentDto.SearchResponse getDocumentList(Integer category, Integer state, Integer sortBy) {
        List<Document> documents = documentRepository.findAllByOption(category, state, sortBy);
        return DocumentDto.SearchResponse.from(documents);
    }

    @Transactional(readOnly = true)
    public DocumentDto.SearchResponse getLikedDocumentList(Integer category, Integer state, Integer sortBy) {
        List<Document> documents;
        if (category != null) {
            documents = documentRepository.findAllByOption(category, state, sortBy);
        } else {
            List<CategoryType> likeCategories = likeCategoryRepository.findByUserId(jwtService.getId())
                    .stream().map(LikeCategory::getCategory).collect(Collectors.toList());
            documents = documentRepository.findAllByLikedCategories(likeCategories, state, sortBy);
        }
        return DocumentDto.SearchResponse.from(documents);
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
