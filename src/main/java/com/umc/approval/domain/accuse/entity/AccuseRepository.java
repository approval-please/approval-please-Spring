package com.umc.approval.domain.accuse.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccuseRepository extends JpaRepository<Accuse, Long>, AccuseRepositoryCustom {
    List<Accuse> findByDocumentId(Long documentId);
}
