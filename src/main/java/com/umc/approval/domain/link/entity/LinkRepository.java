package com.umc.approval.domain.link.entity;
import com.umc.approval.domain.toktok.entity.Toktok;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Long> {
    List<Link> findByToktok(Toktok toktok);
}