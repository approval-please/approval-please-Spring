package com.umc.approval.domain.toktok.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToktokRepository extends JpaRepository<Toktok, Long> {

}
