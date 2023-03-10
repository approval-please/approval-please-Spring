package com.umc.approval.domain.document.entity;

import com.umc.approval.global.type.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long>, DocumentRepositoryCustom {

    @Modifying
    @Query(value = "update document set view = view + 1 where document_id = :document_id", nativeQuery = true)
    void updateView(@Param("document_id") Long documentId);

    @Modifying
    @Query(value = "update document set state = :state where document_id = :document_id", nativeQuery = true)
    void updateState(@Param("document_id") Long documentId, @Param("state") Integer state);

    @Query("select i from Document i where (i.state = 0 or i.state = 1) and i.user.id = :user_id order by i.createdAt desc")
    List<Document> findByUserId(@Param("user_id") Long userId);
    
    @Query("select d from Document d " +
            "join fetch d.user u " +
            "where d.id = :documentId")
    Optional<Document> findByIdWithUser(@Param("documentId") Long documentId);

    // 마이페이지 - 사원증 조회
    @Query(value = "select d from Document d " +
            "where d.user.id = :userId " +
            "order by d.createdAt desc")
    List<Document> findAllByUserId(@Param("userId") Long userId); // 사용자가 작성한 결재서류 전체 조회

    @Query(value = "select d from Document d " +
            "where d.user.id = :userId " +
            "AND d.state = :state " +
            "order by d.createdAt desc")
    List<Document> findAllByState(@Param("userId") Long userId, @Param("state") Integer state); // 사용자가 작성한 결재서류 상태별 조회

    @Query(value = "select d from Document d " +
            "where d.id IN (select a.document.id from Approval a where a.isApprove = :isApproved AND a.user.id = :userId) " +
            "order by d.createdAt desc")
    List<Document> findAllByApproval(@Param("userId") Long userId, @Param("isApproved") Boolean isApproved); // 사용자가 결재한 결재서류 승인별 조회

    // 게시글 목록 조회(페이징 x)
    @Query(value = "select distinct d from Document d " +
            "left join d.tags t " +
            "left join d.images i " +
            "left join d.approvals a " +
            "where d.category = :category "+
            "order by d.createdAt desc")
    List<Document> findAllByCategory(@Param("category") CategoryType category);

    @Query(value = "select distinct d from Document d " +
            "left join d.tags t " +
            "left join d.images i " +
            "left join d.approvals a " +
            "where d.category in (:categories) " +
            "order by d.createdAt desc")
    List<Document> findAllByLikedCategory(@Param("categories") List<CategoryType> categories);

    @Query("select distinct d from Document d " +
            "left join fetch d.approvals a " +
            "join fetch a.user u " +
            "where d.state = :state " +
            "and a.isApprove = :isApproved " +
            "and u.id = :userId " +
            "order by d.createdAt desc")
    List<Document> findAllByStateApproval(@Param("userId") Long userId,
                                          @Param("state") Integer state, @Param("isApproved") Boolean isApproved); // 사용자가 결재한 결재서류 상태별 & 승인별 조회

    @Query("select distinct d from Document d " +
            "left join d.tags t " +
            "left join d.images i " +
            "left join d.approvals a " +
            "order by d.createdAt desc")
    List<Document> findAllWithJoin();

    // 게시글 목록 조회(페이징 o)
    @Query("select distinct d from Document d " +
            "left join d.tags t " +
            "left join d.images i " +
            "left join d.approvals a")
    Page<Document> findAllWithJoinPaging(Pageable pageable);

    @Query(value = "select distinct d from Document d " +
            "left join d.tags t " +
            "left join d.images i " +
            "left join d.approvals a " +
            "where d.category = :category")
    Page<Document> findAllByCategoryPaging(@Param("category") CategoryType category, Pageable pageable);

    @Query(value = "select distinct d from Document d " +
            "left join d.tags t " +
            "left join d.images i " +
            "left join d.approvals a " +
            "where d.category in (:categories)")
    Page<Document> findAllByLikedCategoryPaging(@Param("categories") List<CategoryType> categories, Pageable pageable);

}