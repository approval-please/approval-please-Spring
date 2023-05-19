package com.umc.approval.domain.report.service;

import com.umc.approval.domain.accuse.entity.Accuse;
import com.umc.approval.domain.accuse.entity.AccuseRepository;
import com.umc.approval.domain.approval.entity.Approval;
import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.link.dto.LinkDto;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.performance.entity.Performance;
import com.umc.approval.domain.performance.entity.PerformanceRepository;
import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.report.dto.ReportDto.GetReportResponse;
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
import com.umc.approval.global.type.ReportSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.umc.approval.global.exception.CustomErrorType.*;
import static com.umc.approval.global.type.PerformanceType.WRITE_OTHER_REPORT;
import static com.umc.approval.global.type.PerformanceType.WRITE_REPORT;

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
    private final AccuseRepository accuseRepository;
    private final PerformanceRepository performanceRepository;

    public void createPost(ReportDto.ReportRequest request) {
        User user = certifyUser();

        //결재서류 가져오기
        Document document = findDocument(request.getDocumentId());

        //해당 결재서류에 대한 결재보고서가 이미 존재하는 경우 예외처리
        reportsOnDocumentDuplicateValidation(request.getDocumentId());

        //결재보고서 등록
        Report report = request.toEntity(document);

        reportRepository.save(report);

        //링크 등록
        createLink(request.getLink(), report);

        //태그 등록
        createTag(request.getTag(), report);

        //이미지 등록
        createImages(request.getImages(), report);

        // 작성자 포인트 적립
        writerPointsUp(user);

        // 결재 참여자 포인트 적립
        approveUsersPointsUp(document);
    }

    // 결재보고서 목록 조회
    @Transactional(readOnly = true)
    public ReportDto.SearchResponse getReportList(HttpServletRequest request, Integer sortBy) {
        Long userId = jwtService.getIdDirectHeader(request);
        List<Follow> follows;

        // 팔로우한 사람의 게시글만 조회하는 경우
        if (userId != null
                && sortBy != null
                && sortBy.equals(ReportSortType.FOLLOW.getValue())) {
            follows = followRepository.findMyFollowers(userId);
        } else {
            follows = List.of();
        }

        List<Report> reports = reportRepository.findAllByOption(userId, follows, sortBy);
        return ReportDto.SearchResponse.from(reports);
    }

    // 결재서류 글 작성시 결재서류 선택 리스트
    @Transactional(readOnly = true)
    public ReportDto.ReportGetDocumentResponse selectDocument() {
        User user = certifyUser();

        // 아직 보고서를 작성하지 않은 결재서류 목록 조회
        List<Document> documents = documentRepository.findByUserId(user.getId());
        List<Document> notWriteReportDocuments = documents.stream()
                .filter(document -> reportRepository.findByDocumentId(document.getId()).isEmpty())
                .collect(Collectors.toList());

        // Dto로 변환
        List<ReportDto.DocumentListResponse> response =
                notWriteReportDocuments.stream()
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

        if (user.getId() != document.getUser().getId() || user.getId() != updateDocument.getUser()
                .getId()) {
            throw new CustomException(NO_PERMISSION);
        }

        // 태그 수정
        deleteTag(id);
        createTag(request.getTag(), report);

        // 링크 수정
        deleteLink(id);
        createLink(request.getLink(), report);

        // 이미지 수정
        deleteImages(id);
        createImages(request.getImages(), report);

        report.update(request, updateDocument);
    }

    // 게시글 상세 조회
    public ReportDto.GetReportResponse getReport(Long reportId, HttpServletRequest request) {
        // 조회수 ++
        reportRepository.updateView(reportId);

        // 보고서, 서류, 작성자
        Report report = findReport(reportId);
        Document document = report.getDocument();
        User writer = document.getUser();

        // userId를 활용해 방문 사용자 구하기
        Long userId = jwtService.getIdDirectHeader(request);
        User visitUser;

        // 좋아요, 팔로우, 작성자, 스크랩, 수정 여부 변수 선언
        Boolean likeOrNot;
        Boolean followOrNot;
        Boolean writerOrNot;
        Boolean scrapOrNot;
        boolean isModified; // 수정여부는 null이 될 수 없음

        // 로그인한 유저일 경우
        if (userId != null) {
            // 게시글 상세 조회를 한 유저
            visitUser = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

            // visitUser가 글 작성자인지 여부
            writerOrNot = userId.equals(writer.getId());

            // visitUser가 작성자를 팔로우 했는지 여부
            Long from_userId = visitUser.getId();
            Long to_userId = document.getUser().getId();
            Integer follow = followRepository.countFollowOrNot(from_userId, to_userId);
            if (from_userId.equals(to_userId)) {
                followOrNot = null;
            } else {
                followOrNot = (follow != 0);
            }

            // 해당 유저가 스크랩 했는지 여부
            scrapOrNot = (scrapRepository.countByUserAndReport(visitUser, report) != 0);

            // 해당 유저가 게시글 좋아요를 눌렀는지 여부
            likeOrNot = (likeRepository.countByUserAndReport(visitUser, report) != 0);
        }
        // 비로그인 상태라면
        else {
            writerOrNot = null;
            followOrNot = null;
            scrapOrNot = null;
            likeOrNot = null;
        }

        // 게시글이 수정된 적이 있는 확인
        isModified = !report.getCreatedAt().equals(report.getModifiedAt());

        // 결재서류 정보
        List<String> documentTagList = tagRepository.findTagNameList(document.getId());
        List<String> documentImageUrlList = imageRepository.findImageUrlList(document.getId());
        String documentImageUrl;
        Integer documentImageCount;
            if (documentImageUrlList != null && !documentImageUrlList.isEmpty()) {
            documentImageUrl = documentImageUrlList.get(0);
            documentImageCount = documentImageUrlList.size();
        } else {
            documentImageUrl = null;
            documentImageCount = null;
        }

        // 결재보고서 정보
        List<String> reportTagList = tagRepository.findTagNameListByReportId(reportId);
        List<String> reportImageUrlList = imageRepository.findImageUrlListByReportId(reportId);
        List<Link> reportLinkList = linkRepository.findByReportId(reportId);
        List<LinkDto.Response> linkResponseList =
                reportLinkList.stream().map(LinkDto.Response::fromEntity)
                        .collect(Collectors.toList());

        // 좋아요, 스크랩, 댓글 수
        Long likedCount = likeRepository.countByReport(report);
        Long commentCount = commentRepository.countByReportId(report.getId());
        Long scrapCount = scrapRepository.countByReport(report);

        return new GetReportResponse(
                writer, document, report,
                documentTagList, documentImageUrl, documentImageCount,
                reportTagList, reportImageUrlList, linkResponseList,
                likedCount, scrapCount, commentCount,
                likeOrNot, followOrNot, isModified, writerOrNot, scrapOrNot);
    }

    public void deletePost(Long reportId) {
        User user = certifyUser();
        Report report = findReport(reportId);
        Document document = report.getDocument();

        if (user.getId() != document.getUser().getId()) {
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
        if (likes != null) {
            likeRepository.deleteAll(likes);
        }

        // 스크랩 삭제
        List<Scrap> scraps = scrapRepository.findByReportId(reportId);
        if (scraps != null) {
            scrapRepository.deleteAll(scraps);
        }

        // 댓글 삭제
        List<Comment> comments = commentRepository.findByReportId(reportId);
        if (comments != null) {
            // 댓글 신고 내역 삭제
            List<Long> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());
            accuseRepository.deleteByCommentIds(commentIds);
            commentRepository.deleteAll(comments);
        }

        // 신고 내역 삭제
        List<Accuse> accuses = accuseRepository.findByReportId(reportId);
        if (accuses != null) {
            accuseRepository.deleteAll(accuses);
        }

        reportRepository.deleteById(reportId);
    }

    @Transactional(readOnly = true)
    public ReportDto.SearchResponse search(String query, Integer isTag, Integer category, Integer sortBy) {
        List<Report> reports = reportRepository.findAllByQuery(query, isTag, category, sortBy);
        return ReportDto.SearchResponse.from(reports);
    }

    private User certifyUser() {
        return userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    public void createLink(List<LinkDto.Request> linkList, Report report) {
        if (linkList != null && !linkList.isEmpty()) {
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
    }

    public void createTag(List<String> tagList, Report report) {
        if (tagList != null && !tagList.isEmpty()) {
            for (String tag : tagList) {
                Tag newTag = Tag.builder().report(report).tag(tag).build();
                tagRepository.save(newTag);
            }
        }
    }

    private void createImages(List<String> images, Report report) {
        if (images != null && !images.isEmpty()) {
            for (String imgUrl : images) {
                Image uploadImg = Image.builder().report(report).imageUrl(imgUrl).build();
                imageRepository.save(uploadImg);
            }
        }
    }

    private void writerPointsUp(User user){
        Performance performance = Performance.builder()
                .user(user)
                .content(WRITE_REPORT.getContent())
                .point(WRITE_REPORT.getPoint())
                .build();
        performanceRepository.save(performance);
        user.updatePoint(WRITE_REPORT.getPoint());
    }

    private void approveUsersPointsUp(Document document){
        List<User> approveUsers = approvalRepository.findAllUserByApproval(document.getId())
                .stream().map(Approval::getUser).collect(Collectors.toList());

        approveUsers.forEach(u -> {
            Performance p = Performance.builder()
                    .user(u)
                    .content(WRITE_OTHER_REPORT.getContent())
                    .point(WRITE_OTHER_REPORT.getPoint())
                    .build();
            performanceRepository.save(p);
            u.updatePoint(WRITE_OTHER_REPORT.getPoint());
        });
    }

    private Document findDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new CustomException(DOCUMENT_NOT_FOUND));
    }

    private Report findReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));
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

    private void reportsOnDocumentDuplicateValidation(Long documentId) {
        reportRepository.findByDocumentId(documentId)
                .ifPresent(report -> {
                    throw new CustomException(REPORT_ALREADY_EXISTS);
                });
    }
}