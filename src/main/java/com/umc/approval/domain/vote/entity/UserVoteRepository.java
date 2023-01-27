package com.umc.approval.domain.vote.entity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserVoteRepository extends JpaRepository<UserVote, Long> {

    @Query("select count(distinct user_id) from UserVote where vote_id = :vote_id")
    Integer findVotePeople(@Param("vote_id") Long voteId);

//    @Query(value = "select count(*) from user_vote where user_vote.vote_id = :vote_id group by user_vote.vote_option_id", nativeQuery = true)
//    List<Integer> findPeopleEachOption(@Param("vote_id") Long voteId);


//    @Query(value = "select count(*) from UserVote u "
//        + "join u.vote v "
//        + "right join u.voteOption vo "
//        + "where v.id = :vote_id "
//        + "group by vo.id")
//    List<Integer> findPeopleEachOption(@Param("vote_id") Long voteId);


    @Query("select uv from UserVote uv " +
        "join fetch uv.voteOption o " +
        "join uv.user u " +
        "join uv.vote v " +
        "where u.id = :user_id " +
        "and v.id = :vote_id")
    List<UserVote> findAllByUserAndVote(@Param("user_id") Long userId, @Param("vote_id") Long voteId);

    void deleteByVoteId(Long voteId);
}
