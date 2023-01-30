package com.umc.approval.domain.report.entity;

import com.umc.approval.domain.document.entity.Document;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {
    Optional<Report> findByDocumentId(Long documentId);

    @Query("select r from Report r " +
            "join fetch r.document d " +
            "join fetch d.user u " +
            "where r.id = :reportId")
    Optional<Report> findByIdWithUser(@Param("reportId") Long reportId);

    @Modifying
    @Query(value = "update report set view = view + 1 where report_id = :report_id", nativeQuery = true)
    void updateView(@Param("report_id") Long report_id);

    @Query("select r from Report r " +
            "join r.document d " +
            "join d.user u " +
            "where u.id = :user_id " +
            "order by r.createdAt desc")
    List<Report> findAllByUserId(@Param("user_id") Long userId);

    @Query("select r from Report r " +
            "join r.document d " +
            "join d.user u " +
            "where u.id in (:user_id_list) " +
            "order by r.createdAt desc")
    List<Report> findAllByUserIdList(@Param("user_id_list") List<Long> userIdList);
}
