package com.umc.approval.domain.comment.entity;

import com.umc.approval.domain.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> findAllByPost(Long documentId, Long toktokId, Long reportId);

    Page<Comment> findAllByPostPaging(Pageable pageable, Long documentId, Long toktokId, Long reportId);

    Integer countByPost(Long documentId, Long toktokId, Long reportId);
}
