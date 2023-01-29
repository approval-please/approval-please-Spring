package com.umc.approval.domain.report.service;

import static com.umc.approval.global.exception.CustomErrorType.*;

import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.link.dto.LinkDto;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.report.dto.ReportDto.GetReportResponse;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.scrap.entity.Scrap;
import com.umc.approval.domain.scrap.entity.ScrapRepository;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class ReportService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final LinkRepository linkRepository;
    private final TagRepository tagRepository;
    private final ReportRepository reportRepository;
    private final DocumentRepository documentRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ScrapRepository scrapRepository;
    private final FollowRepository followRepository;
    private final ApprovalRepository approvalRepository;

    public void createPost(ReportDto.ReportRequest request) {

        //결재서류 가져오기
        Document document = findDocument(request.getDocumentId());

        //해당 결재서류에 대한 결재보고서가 이미 존재하는 경우
        Optional<Report> getReport = reportRepository.findByDocumentId(request.getDocumentId());
        if (getReport.isPresent()) {
            throw new CustomException(REPORT_ALREADY_EXISTS);
        }

        //결재보고서 등록
        Report report = Report.builder()
            .content(request.getContent())
            .document(document)
            .notification(true)
            .view(0L)
            .build();

        reportRepository.save(report);

        //링크 등록
        if (request.getLink() != null && !request.getLink().isEmpty()) {
            createLink(request.getLink(), report);
        }

        //태그 등록
        if (request.getTag() != null) {
            createTag(request.getTag(), report);
        }

        //이미지 등록
        createImages(request.getImages(), report);

        // 작성자 포인트 적립
        userRepository.updatePoint(document.getUser().getId(),500L);
        // 결재 참여자 포인트 적립
        List<Long> userIdList = approvalRepository.findByDocumentId(document.getId());
        userRepository.updatePoint(userIdList, 200L);
    }

    // 결재서류 글 작성시 결재서류 선택 리스트
    public ReportDto.ReportGetDocumentResponse selectDocument() {
        User user = certifyUser();

        List<Document> documents = documentRepository.findByUserId(user.getId());

        // Dto로 변환
        List<ReportDto.DocumentListResponse> response;

        response = documents.stream()
                .map(ReportDto.DocumentListResponse::fromEntity)
                .collect(Collectors.toList());
        response = documents.stream()
            .map(ReportDto.DocumentListResponse::fromEntity)
            .collect(Collectors.toList());

        return ReportDto.ReportGetDocumentResponse.from(response);
    }

    public void updatePost(Long id, ReportDto.ReportRequest request) {
        User user = certifyUser();
        Report report = findReport(id);
        // 현재 결재서류
        Document document = report.getDocument();

        // 변경된 결재서류
        Document updateDocument = findDocument(request.getDocumentId());

        if (user.getId() != document.getUser().getId() || user.getId() != updateDocument.getUser().getId()) {
            throw new CustomException(NO_PERMISSION);
        }

        // 태그 수정
        deleteTag(id);
        if (request.getTag() != null) {
            List<String> tagList = request.getTag();
            if (tagList != null && !tagList.isEmpty()) {
                createTag(tagList, report);
            }
        }

        // 링크 수정
        List<Link> links = linkRepository.findByReportId(report.getId());
        if (links != null && !links.isEmpty()) {
            linkRepository.deleteAll(links);
        }
        if (request.getLink() != null && !request.getLink().isEmpty()) {
            createLink(request.getLink(), report);
        }

        // 이미지 수정
        List<Image> images = imageRepository.findByReportId(report.getId());
        if (images != null && !images.isEmpty()) {
            imageRepository.deleteAll(images);
        }
        createImages(request.getImages(), report);

        report.update(request, updateDocument);

    }

    // 게시글 상세 조회
    public ReportDto.GetReportResponse getReport(Long reportId) {
        reportRepository.updateView(reportId);

        Report report = findReport(reportId);
        Document document = report.getDocument();
        User user = document.getUser();

        // 결재서류 정보
        List<String> documentTagList = tagRepository.findTagNameList(document.getId());
        List<String> documentImageUrlList = imageRepository.findImageUrlList(document.getId());

        // 결재보고서 정보
        List<String> reportTagList = tagRepository.findTagNameListByReportId(reportId);
        List<String> reportImageUrlList = imageRepository.findImageUrlListByReportId(reportId);
        List<Link> reportLinkList = linkRepository.findByReportId(reportId);
        List<LinkDto.Response> linkResponse;
        linkResponse = reportLinkList.stream().map(LinkDto.Response::fromEntity).collect(Collectors.toList());


        // 좋아요, 스크랩, 댓글 수
        Long likedCount = likeRepository.countByReport(report);
        Long commentCount = commentRepository.countByReportId(report.getId());
        Long scrapCount = scrapRepository.countByReport(report);
        Long likeReportOrNot = likeRepository.countByUserAndReport(user, report);
        Boolean likeOrNot = true;
        Boolean followOrNot = true;

        Boolean isModified = true;
        // 게시글이 수정된 적이 있는 확인
        if (report.getCreatedAt() == report.getModifiedAt()) {
            isModified = false;
        }

        // 해당 유저가 게시글을 눌렀는지 여부
        if(likeReportOrNot == 0) {
            likeOrNot = false;
        }

        // 게시글 상세 조회를 한 유저가 글을 쓴 유저를 팔로우 했는지 여부
        Long from_userId = user.getId();
        Long to_userId = document.getUser().getId();
        Integer follow = followRepository.countFollowOrNot(from_userId, to_userId);
        if (from_userId == to_userId) {
            followOrNot = null;
        } else if(follow == 0) {
            followOrNot = false;
        }
        return new GetReportResponse(user, document, report,
            documentTagList, documentImageUrlList,
            reportTagList, reportImageUrlList,
            linkResponse, likedCount, scrapCount, commentCount, likeOrNot, followOrNot, isModified);
    }

    public void deletePost(Long reportId) {
        User user = certifyUser();
        Report report = findReport(reportId);
        Document document = report.getDocument();

        if(user.getId() != document.getUser().getId()) {
            throw new CustomException(NO_PERMISSION);
        }

        //태그 삭제
        deleteTag(reportId);

        //이미지 삭제
        deleteImages(reportId);

        //링크 삭제
        deleteLink(reportId);

        //좋아요 삭제
        List<Like> likes = likeRepository.findByReportId(reportId);
        if(likes != null) {
            likeRepository.deleteAll(likes);
        }

        // 스크랩 삭제
        List<Scrap> scraps = scrapRepository.findByReportId(reportId);
        if(scraps != null) {
            scrapRepository.deleteAll(scraps);
        }

        // 댓글 삭제
        List<Comment> comments = commentRepository.findByReportId(reportId);
        if(comments != null) {
            commentRepository.deleteAll(comments);
        }

        reportRepository.deleteById(reportId);


    }

    @Transactional(readOnly = true)
    public ReportDto.SearchResponse search(String query, Integer isTag, Integer category, Integer sortBy) {
        List<Report> reports = reportRepository.findAllByQuery(query, isTag, category, sortBy);
        return ReportDto.SearchResponse.from(reports);
    }

    private User certifyUser() {
        User user = userRepository.findById(jwtService.getId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        return user;
    }

    public void createLink(List<LinkDto.Request> linkList, Report report) {
        for (LinkDto.Request l : linkList) {
            Link link = Link.builder()
                .report(report)
                .url(l.getUrl())
                .title(l.getTitle())
                .image(l.getImage())
                .build();
            linkRepository.save(link);
        }
    }

    public void createTag(List<String> tagList, Report report) {
        for (String tag : tagList) {
            Tag newTag = Tag.builder().report(report).tag(tag).build();
            tagRepository.save(newTag);
        }
    }

    private Document findDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new CustomException(DOCUMENT_NOT_FOUND));

        return document;
    }

    private Report findReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        return report;
    }

    private void createImages(List<String> images, Report report) {
        if (images != null && !images.isEmpty()) {
            for (String imgUrl : images) {
                Image uploadImg = Image.builder().report(report).imageUrl(imgUrl).build();
                imageRepository.save(uploadImg);
            }
        }
    }

    private void deleteTag(Long reportId) {
        List<Tag> tagList = tagRepository.findByReportId(reportId);
        if (tagList != null) {
            tagRepository.deleteAll(tagList);
        }
    }

    private void deleteLink(Long reportId) {
        List<Link> linkList = linkRepository.findByReportId(reportId);
        if (linkList != null) {
            linkRepository.deleteAll(linkList);
        }
    }

    private void deleteImages(Long reportId) {
        List<Image> imageList = imageRepository.findByReportId(reportId);
        if (imageList != null) {
            imageRepository.deleteAll(imageList);
        }
    }
}
