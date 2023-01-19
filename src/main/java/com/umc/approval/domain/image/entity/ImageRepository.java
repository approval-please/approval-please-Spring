package com.umc.approval.domain.image.entity;
import com.umc.approval.domain.toktok.entity.Toktok;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByToktok(Toktok toktok);
}
