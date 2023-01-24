package com.umc.approval.domain.report.service;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        if (request.getLinkUrl() != null) {
            createLink(request.getLinkUrl(), report);
        }

        //태그 등록
        if (request.getTag() != null) {
            createTag(request.getTag(), report);
        }

        //이미지 등록
        createImages(request.getImages(), report);
    }

    // 결재서류 글 작성시 결재서류 선택 리스트
    public ReportDto.ReportGetDocumentResponse selectDocument(Integer page) {
        User user = certifyUser();

        //페이징
        Pageable pageable =
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Document> documents = documentRepository.findByUserId(user.getId(), pageable);

        // Dto로 변환
        List<ReportDto.DocumentListResponse> response;
        response = documents.getContent().stream()
                .map(ReportDto.DocumentListResponse::fromEntity)
                .collect(Collectors.toList());

        return ReportDto.ReportGetDocumentResponse.from(documents, response);
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
        List<Tag> tags = tagRepository.findByReportId(report.getId());
        if (tags != null && !tags.isEmpty()) {
            tagRepository.deleteAll(tags);
        }
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
        if (request.getLinkUrl() != null) {
            List<String> linkList = request.getLinkUrl();
            if (linkList != null && !linkList.isEmpty()) {
                createLink(linkList, report);
            }
        }

        // 이미지 수정
        List<Image> images = imageRepository.findByReportId(report.getId());
        if (images != null && !images.isEmpty()) {
            imageRepository.deleteAll(images);
        }
        createImages(request.getImages(), report);

        report.update(request, updateDocument);

    }


    private User certifyUser() {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        return user;
    }

    public void createLink(List<String> linkList, Report report) {
        for (String link : linkList) {
            Link newLink = Link.builder().report(report).linkUrl(link).build();
            linkRepository.save(newLink);
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
}
