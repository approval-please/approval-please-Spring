package com.umc.approval.domain.scrap.entity;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
import java.util.List;

import com.umc.approval.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long>, ScrapRepositoryCustom{

    Long countByReport(Report report);

    Long countByToktok(Toktok toktok);

    List<Scrap> findByReportId(Long reportId);

    List<Scrap> findByToktokId(Long toktokId);

    List<Scrap> findByDocumentId(Long documentId);


    Long countByUserAndDocument(User user, Document document);

    Long countByUserAndToktok(User user, Toktok toktok);

    Long countByUserAndReport(User user, Report report);


}
