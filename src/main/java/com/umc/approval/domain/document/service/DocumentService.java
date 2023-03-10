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
import com.umc.approval.domain.performance.entity.Performance;
import com.umc.approval.domain.performance.entity.PerformanceRepository;
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
import static com.umc.approval.global.type.PerformanceType.WRITE_DOCUMENT;

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
    private final PerformanceRepository performanceRepository;

    public void createDocument(DocumentDto.DocumentRequest request) {
        User user = certifyUser();

        CategoryType categoryType = findCategory(request.getCategory());

        // ????????? ??????
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

        // ??????, ????????? ????????????
        Performance performance = Performance.builder()
                .user(user)
                .content(WRITE_DOCUMENT.getContent())
                .point(WRITE_DOCUMENT.getPoint())
                .build();
        performanceRepository.save(performance);
        user.updatePoint(WRITE_DOCUMENT.getPoint());
    }

    public DocumentDto.GetDocumentResponse getDocument(HttpServletRequest request, Long documentId) {

        // ?????? ??? ????????????
        documentRepository.updateView(documentId);

        // ???????????? ??????
        Document document = findDocument(documentId);
        User writer = document.getUser(); // ???????????? ?????????
        List<String> tagNameList = tagRepository.findTagNameList(documentId);
        List<String> imageUrlList = imageRepository.findImageUrlList(documentId);
        Link link = linkRepository.findByDocumentId(documentId).orElse(null);

        // ??????, ?????? ???
        int approveCount = approvalRepository.countApproveByDocumentId(documentId);
        int rejectCount = approvalRepository.countRejectByDocumentId(documentId);

        // ????????? ???, ?????? ???
        int likedCount = likeRepository.countByDocumentId(documentId);
        int commentCount = commentRepository.countByDocumentId(documentId);

        // ????????? ?????? ??????
        boolean isModified = document.getCreatedAt().isEqual(document.getModifiedAt()) ? false : true;

        // ????????? ?????????, ?????????/????????? ??????
        boolean isWriter = false;
        boolean isLiked = false;
        boolean isScrap = false;
        int isVoted = 0;
        Long userId = jwtService.getIdDirectHeader(request); // ????????? ??? ?????????

        // ????????? o
        if (userId != null) {
            User user = userRepository.findById(userId).get();

            // ????????? ???????????????
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

                // ???????????? ???????????? ??? ???????????? ?????? ??????/?????? ??????
                Optional<Approval> approval = approvalRepository.findByUserAndDocument(user, document);
                if(approval != null && !approval.isEmpty()){ // ??????/?????? ????????? ??????
                    if(approval.get().getIsApprove() == true)
                        isVoted = 1;
                    else // ??????
                        isVoted = 2;
                }
            }

            // ????????? ?????????/????????? ??????
            isLiked = likeRepository.countByUserAndDocument(user, document) == 0 ? false : true;
            isScrap = scrapRepository.countByUserAndDocument(user, document) == 0 ? false : true;
        }

        // ???????????? ?????????????????? ??????????????????
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
        // ????????? ?????? ??????, ?????? ?????? ??????
        Document document = findDocument(documentId);
        User user = certifyUser();
        if (user.getId() != document.getUser().getId()) {
            throw new CustomException(NO_PERMISSION);
        }

        // document ??????
        CategoryType categoryType = findCategory(request.getCategory());

        document.update(categoryType, request.getTitle(), request.getContent());

        // tag ??????
        deleteTag(documentId);
        createTag(request.getTag(), document);

        // image ??????
        deleteImages(documentId);
        createImages(request.getImages(), document);

        // link ??????
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
        // ????????? ?????? ??????, ?????? ?????? ??????
        Document document = findDocument(documentId);
        User user = certifyUser();
        if (user.getId() != document.getUser().getId()) {
            throw new CustomException(NO_PERMISSION);
        }

        // tag ??????
        deleteTag(documentId);

        // image ??????
        deleteImages(documentId);

        // link ??????
        linkRepository.findByDocumentId(documentId).ifPresent(linkRepository::delete);

        // ?????? ??????
        List<Comment> commentList = commentRepository.findByDocumentId(documentId);
        if (commentList != null) {
            // ?????? ?????? ??????
            List<Long> commentIdList = commentList.stream()
                    .map(Comment::getId).collect(Collectors.toList());
            List<Accuse> accuseList = accuseRepository.findByCommentId(commentIdList);
            if(accuseList != null){
                accuseRepository.deleteAll(accuseList);
            }
            // ?????? ??????
            commentRepository.deleteAll(commentList);
        }

        // ????????? ??????
        List<Like> likedList = likeRepository.findByDocumentId(documentId);
        if (likedList != null) {
            likeRepository.deleteAll(likedList);
        }

        // ????????? ??????
        List<Scrap> scrapList = scrapRepository.findByDocumentId(documentId);
        if (scrapList != null) {
            scrapRepository.deleteAll(scrapList);
        }

        // ??????/?????? ??????
        List<Approval> approvalList = approvalRepository.findByDocumentId(documentId);
        if(approvalList != null){
            approvalRepository.deleteAll(approvalList);
        }

        // ??????????????? ??? ?????? ?????? ?????? ??????
        reportRepository.findByDocumentId(documentId).ifPresent(r -> reportService.deletePost(r.getId()));

        // ?????? ?????? ??????
        List<Accuse> accuseList = accuseRepository.findByDocumentId(documentId);
        if(accuseList != null){
            accuseRepository.deleteAll(accuseList);
        }

        // document ??????
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
