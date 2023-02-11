package com.umc.approval.domain.scrap.entity;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScrapRepository extends JpaRepository<Scrap, Long>, ScrapRepositoryCustom{

    Long countByReport(Report report);

    Long countByToktok(Toktok toktok);

    List<Scrap> findByReportId(Long reportId);

    List<Scrap> findByToktokId(Long toktokId);

    List<Scrap> findByDocumentId(Long documentId);


    Long countByUserAndDocument(User user, Document document);

    Long countByUserAndToktok(User user, Toktok toktok);

    Long countByUserAndReport(User user, Report report);

    @Query("select distinct d from Document d join Scrap s on s.document.id = d.id order by s.createdAt desc")
    List<Document> findDocuments(Long userId);

    @Query("select s from Scrap s join fetch s.document d where s.user.id = :userId and d.state = :state order by s.createdAt desc")
    List<Scrap> findScrapsByUserAndState(Long userId, Integer state);

    @Query("select distinct t from Toktok t join Scrap s on s.toktok.id = t.id order by s.createdAt desc")
    List<Toktok> findToktoks(Long userId);

    @Query("select distinct r from Report r join Scrap s on s.report.id = r.id order by s.createdAt desc")
    List<Report> findReports(Long userId);
}
