package com.umc.approval.domain.tag.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
