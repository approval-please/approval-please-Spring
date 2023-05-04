package com.umc.approval.domain.scrap.service;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.scrap.dto.ScrapDto;
import com.umc.approval.domain.scrap.entity.Scrap;
import com.umc.approval.domain.scrap.entity.ScrapRepository;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class ScrapService {

    private final JwtService jwtService;
    private final ScrapRepository scrapRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final ToktokRepository toktokRepository;
    private final ReportRepository reportRepository;

    public ScrapDto.UpdateResponse scrap(ScrapDto.Request scrapRequest) {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Optional<Scrap> findScrap = scrapRepository.findByUserAndPost(user.getId(), scrapRequest);

        // 스크랩 추가 되어있는 경우 -> 취소
        if (findScrap.isPresent()) {
            scrapRepository.delete(findScrap.get());
            return new ScrapDto.UpdateResponse(false);
        }

        // 스크랩 추가
        Document document = null;
        Toktok toktok = null;
        Report report = null;

        if (scrapRequest.getDocumentId() != null) {
            document = documentRepository.findById(scrapRequest.getDocumentId())
                    .orElseThrow(() -> new CustomException(DOCUMENT_NOT_FOUND));
        } else if (scrapRequest.getToktokId() != null) {
            toktok = toktokRepository.findById(scrapRequest.getToktokId())
                    .orElseThrow(() -> new CustomException(TOKTOKPOST_NOT_FOUND));
        } else {
            report = reportRepository.findById(scrapRequest.getReportId())
                    .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));
        }

        Scrap scrap = scrapRequest.toEntity(user, document, toktok, report);
        scrapRepository.save(scrap);

        return new ScrapDto.UpdateResponse(true);
    }
}
