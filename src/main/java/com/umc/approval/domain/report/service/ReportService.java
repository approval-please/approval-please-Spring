package com.umc.approval.domain.report.service;

import static com.umc.approval.global.exception.CustomErrorType.DOCUMENT_NOT_FOUND;
import static com.umc.approval.global.exception.CustomErrorType.TOKTOKPOST_NOT_FOUND;
import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;

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
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.aws.service.AwsS3Service;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequiredArgsConstructor
@Service
public class ReportService {

    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;
    private final UserRepository userRepository;
    private final LinkRepository linkRepository;
    private final TagRepository tagRepository;
    private final ReportRepository reportRepository;
    private final DocumentRepository documentRepository;
    private final ImageRepository imageRepository;

    public void createPost(ReportDto.ReportRequest request, List<MultipartFile> files) {
        User user = certifyUser();

        //결재서류 가져오기
        Document document = findDocument(request.getDocumentId());

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
            List<String> tagList = request.getTag();
            createTag(request.getTag(), report);
        }

        //이미지 등록
        createImages(files, report);
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

    private Document findDocument(Long documentId){
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new CustomException(DOCUMENT_NOT_FOUND));

        return document;
    }

    private void createImages(List<MultipartFile> images, Report report){
        if (images != null && !images.isEmpty()) {
            if (images.size() == 1) {
                String imgUrl = awsS3Service.uploadImage(images.get(0));
                Image uploadImg = Image.builder().report(report).imageUrl(imgUrl).build();
                imageRepository.save(uploadImg);
            } else {
                List<String> imgUrls = awsS3Service.uploadImage(images);
                for (String imgUrl : imgUrls) {
                    Image uploadImg = Image.builder().report(report).imageUrl(imgUrl).build();
                    imageRepository.save(uploadImg);
                }
            }
        }
    }

}
