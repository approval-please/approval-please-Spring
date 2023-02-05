package com.umc.approval.domain.accuse.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccuseRepository extends JpaRepository<Accuse, Long>, AccuseRepositoryCustom {
    List<Accuse> findByReportId(Long reportId);
    List<Accuse> findByToktokId(Long toktokId);
}
