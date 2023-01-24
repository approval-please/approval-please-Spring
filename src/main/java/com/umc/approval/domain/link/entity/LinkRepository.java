package com.umc.approval.domain.link.entity;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface LinkRepository extends JpaRepository<Link, Long> {
    @Query("select l from Link l where l.toktok.id = :toktokId")
    List<Link> findByToktokId(@Param("toktokId") Long toktokId);

    List<Link> findByReportId(Long reportId);

    @Query(value = "select link_url from link where report_id = :report_id", nativeQuery = true)
    List<String> findLinkUrlList(@Param("report_id") Long reportId);
}

