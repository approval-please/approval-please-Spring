package com.umc.approval.domain.accuse.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccuseRepository extends JpaRepository<Accuse, Long>, AccuseRepositoryCustom {

}
