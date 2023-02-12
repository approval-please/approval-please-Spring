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

    @Query("select s from Scrap s join fetch s.document d where s.user.id = :userId order by s.createdAt desc")
    List<Scrap> findScrapsByUserAndDocument(Long userId);

    @Query("select s from Scrap s join fetch s.document d where s.user.id = :userId and d.state = :state order by s.createdAt desc")
    List<Scrap> findScrapsByUserAndDocumentAndState(Long userId, Integer state);

    @Query("select s from Scrap s join fetch s.toktok t where s.user.id = :userId order by s.createdAt desc")
    List<Scrap> findScrapsByUserAndToktok(Long userId);

    @Query("select s from Scrap s join fetch s.report r where s.user.id = :userId order by s.createdAt desc")
    List<Scrap> findScrapsByUserAndReport(Long userId);
}
