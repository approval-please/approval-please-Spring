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

    Long countByUserAndToktok(User user, Toktok toktok);

    Long countByUserAndReport(User user, Report report);

    @Query("select d from Document d where d.id IN (select c.document.id from Comment c where c.user.id = :userId)")
    List<Document> findDocuments(Long userId);

    @Query("select d from Document d where d.id IN (select c.document.id from Comment c where c.user.id = :userId) AND d.state = :state")
    List<Document> findDocumentsByState(Long userId, Integer state);

    @Query("select t from Toktok t where t.id IN(select c.toktok.id from Comment c where c.user.id = :userId)")
    List<Toktok> findToktoks(Long userId);

    @Query("select r from Report r where r.id IN (select c.report.id from Comment c where c.user.id = :userId)")
    List<Report> findReports(Long userId);
}
