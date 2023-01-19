package com.umc.approval.domain.tag.entity;

import com.umc.approval.domain.toktok.entity.Toktok;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByToktok(Toktok tok);
}
