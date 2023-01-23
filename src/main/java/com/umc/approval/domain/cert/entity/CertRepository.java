package com.umc.approval.domain.cert.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertRepository extends JpaRepository<Cert, Long> {
    Optional<Cert> findByPhoneNumber(String phoneNumber);

    void deleteByPhoneNumber(String phoneNumber);
}
