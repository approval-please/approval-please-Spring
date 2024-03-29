package com.umc.approval.domain.toktok.service;


import com.umc.approval.domain.accuse.entity.Accuse;
import com.umc.approval.domain.accuse.entity.AccuseRepository;
import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.link.dto.LinkDto;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.scrap.entity.Scrap;
import com.umc.approval.domain.scrap.entity.ScrapRepository;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.toktok.dto.ToktokDto.GetVotePeopleListResponse;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.domain.vote.entity.*;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class ToktokService {

    private final JwtService jwtService;
    private final ToktokRepository toktokRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final LinkRepository linkRepository;
    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;
    private final EntityManager entityManager;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;
    private final ScrapRepository scrapRepository;
    private final CommentRepository commentRepository;
    private final UserVoteRepository userVoteRepository;
    private final AccuseRepository accuseRepository;


    public void createPost(ToktokDto.PostToktokRequest request) {
        User user = certifyUser();

        //투표 등록
        Vote vote = null;
        if (request.getVoteTitle() != null) {
            vote = createVote(request);
            createVoteOption(request.getVoteOption(), vote);
        }

        //카테고리 등록
        CategoryType categoryType = viewCategory(request.getCategory());

        //결제톡톡 게시글 등록
        Toktok toktok = request.toEntity(user, categoryType, vote);
        toktokRepository.save(toktok);

        //링크 등록
        createLink(request.getLink(), toktok);

        //태그 등록
        createTag(request.getTag(), toktok);

        //이미지 등록
        createImages(request.getImages(), toktok);
    }

    @Transactional(readOnly = true)
    public ToktokDto.SearchResponse getToktokList(HttpServletRequest request, Integer sortBy) {
        Long userId = jwtService.getIdDirectHeader(request);
        List<Follow> follows = List.of();
        if (userId != null && sortBy != null && sortBy == 1) {
            follows = followRepository.findMyFollowers(userId);
        }
        List<Toktok> toktoks = toktokRepository.findAllByOption(userId, follows, sortBy);
        return ToktokDto.SearchResponse.from(toktoks);
    }

    public ToktokDto.GetToktokResponse getToktok(Long toktokId, HttpServletRequest request) {
        toktokRepository.updateView(toktokId);

        // 결재톡톡 정보
        Toktok toktok = findToktok(toktokId);

        List<String> tags = tagRepository.findTagNameListByToktokId(toktokId);
        List<String> images = imageRepository.findImageUrlListBytoktokId(toktokId);
        List<Link> reportLinkList = linkRepository.findByToktokId(toktokId);
        List<LinkDto.Response> linkResponse = reportLinkList.stream().map(LinkDto.Response::fromEntity).collect(Collectors.toList());
        Boolean isModified = true;
        // 게시글이 수정된 적이 있는 확인
        if (toktok.getCreatedAt() == toktok.getModifiedAt()) {
            isModified = false;
        }

        // 유저정보(글쓴이, 조회한 사람)
        Long userId = jwtService.getIdDirectHeader(request);
        User visitUser = null;
        if (userId != null) {
            visitUser = userRepository.findById(userId).get();
        }
        User writer = toktok.getUser();

        // 투표 정보
        Vote vote = toktok.getVote();
        List<ToktokDto.VoteOptionResponse> voteOption = null;
        List<ToktokDto.VoteOptionResponse> voteSelect = null;
        List<Long> votePeopleEachOption = null;
        Integer votePeople = null;
        if (vote != null) {
            voteOption = vote.getVoteOptions().stream()
                .map(ToktokDto.VoteOptionResponse::fromEntity)
                .collect(Collectors.toList());
            votePeople = userVoteRepository.findVotePeopleCount(vote.getId());  // 투표 총 참여자 수
        }

        // 좋아요, 스크랩, 댓글 수
        Long likedCount = likeRepository.countByToktok(toktok);
        Long commentCount = commentRepository.countByToktokId(toktok.getId());
        Long scrapCount = scrapRepository.countByToktok(toktok);
        Boolean likeOrNot = true;
        Boolean followOrNot = true;
        Boolean scrapOrNot = true;
        Boolean writerOrNot = false;

        // 해당 유저가 스크랩 했는지 여부
        scrapOrNot = scrapRepository.countByUserAndToktok(visitUser, toktok) == 0 ? false : true;

        if (userId != null) {
            //로그인 한 사용자
            User user = userRepository.findById(userId).get();
            if (vote != null) {
                voteSelect = userVoteRepository.findAllByUserAndVote(user.getId(), vote.getId())
                    .stream()
                    .map(uv -> ToktokDto.VoteOptionResponse.fromEntity(uv.getVoteOption()))
                    .collect(Collectors.toList());
                List<Long> voteOptionIds = vote.getVoteOptions().stream()
                    .map(VoteOption::getId).collect(Collectors.toList());
                votePeopleEachOption = voteOptionIds.stream().map(id ->
                    vote.getUserVotes().stream()
                        .filter(uv ->
                            uv.getVoteOption().getId() == id)
                        .count())
                    .collect(Collectors.toList());
            }
        } else {
            return new ToktokDto.GetToktokResponse(writer, toktok, vote, tags,
                images, linkResponse, likedCount,
                commentCount, scrapCount, null,
                null, voteOption, null,
                votePeople, null, null, isModified, null);
        }

        // 게시글 조회한 유저가 게시글 작성자인지 여부
        if (userId == writer.getId()) {
            writerOrNot = true;
            voteSelect = null;
        }

        // 해당 유저가 게시글을 눌렀는지 여부
        likeOrNot = likeRepository.countByUserAndToktok(visitUser, toktok) == 0 ? false : true;

        // 게시글 상세 조회를 한 유저가 글을 쓴 유저를 팔로우 했는지 여부
        Long from_userId = userId;
        Long to_userId = toktok.getUser().getId();
        Integer follow = followRepository.countFollowOrNot(from_userId, to_userId);
        if (from_userId == to_userId) {
            followOrNot = null;
        } else if (follow == 0) {
            followOrNot = false;
        }

        return new ToktokDto.GetToktokResponse(writer, toktok, vote, tags,
            images, linkResponse, likedCount,
            commentCount, scrapCount, likeOrNot,
            followOrNot, voteOption, voteSelect,
            votePeople, votePeopleEachOption, writerOrNot, isModified, scrapOrNot);
    }

    public void updatePost(Long id, ToktokDto.PostToktokRequest request) {
        User user = certifyUser();
        Toktok toktok = findToktok(id);

        if (user.getId() != toktok.getUser().getId()) {
            throw new CustomException(NO_PERMISSION);
        }

        // 태그 수정
        deleteTag(id);
        createTag(request.getTag(), toktok);

        // 링크 수정
        deleteLink(id);
        createLink(request.getLink(), toktok);

        CategoryType categoryType = viewCategory(request.getCategory());

        // 투표가 종료된 글의 투표 관련 사항을 수정하려는 경우
        if (toktok.getVote() != null && toktok.getVote().getIsEnd().equals(true) && (request.getVoteTitle() != null
            || request.getVoteOption() != null ||
            request.getVoteIsSingle() != null || request.getVoteIsAnonymous() != null)) {
            throw new CustomException(VOTE_IS_END);
        }

        // 없었던 투표를 새로 생성하는 경우
        if (toktok.getVote() == null && request.getVoteTitle() != null) {
            Vote vote = createVote(request);
            createVoteOption(request.getVoteOption(), vote);
            toktok.update(request, categoryType, vote);
        } else if (toktok.getVote() != null) {
            Vote getVote = toktok.getVote();
            userVoteRepository.deleteByVoteId(getVote.getId());
            voteOptionRepository.deleteByVoteId(getVote.getId());
            // 있었던 투표를 없애는 경우
            if (getVote.getTitle() != null && request.getVoteTitle() == null) {
                toktok.deleteVote();
                entityManager.flush();
                entityManager.clear();
                voteRepository.delete(getVote);
            } else if (toktok.getVote() != null && request.getVoteTitle() != null) {
                // 있었던 투표를 수정하는 경우
                getVote.update(request);
                createVoteOption(request.getVoteOption(), getVote);
            }
            toktok.update(request, categoryType, getVote);
        } else {
            // 없는 투표를 새로 만들지 않는 경우
            toktok.update(request, categoryType, null);
        }

        // 이미지 수정
        deleteImage(id);
        createImages(request.getImages(), toktok);
    }

    // 투표하기
    public ToktokDto.VotePeopleEachOptionResponse vote(ToktokDto.VoteRequest request, Long voteId) {
        User user = certifyUser();
        List<Long> voteOptionIds = request.getVoteOptionIds();
        Vote vote = voteRepository.findById(voteId).get();
        List<Long> findOptions = vote.getVoteOptions().stream()
            .map(VoteOption::getId).collect(Collectors.toList());

        // 다시 투표할 수도 있으므로 우선 투표내역 삭제
        userVoteRepository.deleteByVoteIdAndUserId(user.getId(), vote.getId());

        if (!findOptions.containsAll(voteOptionIds)) {
            throw new CustomException(NOT_MATCH_WITH_VOTE);
        }

        for(int i=0; i<voteOptionIds.size(); i++) {
            VoteOption voteOption = voteOptionRepository.findById(voteOptionIds.get(i)).get();
            UserVote userVote = UserVote.builder()
                .user(user)
                .vote(vote)
                .voteOption(voteOption)
                .build();
            userVoteRepository.save(userVote);
        }

        // 각 선택지마다 투표한 사람 수
        List<Long> votePeopleEachOption = findOptions.stream().map(id ->
            vote.getUserVotes().stream()
                .filter(uv ->
                    uv.getVoteOption().getId() == id)
                .count())
            .collect(Collectors.toList());

        return new ToktokDto.VotePeopleEachOptionResponse(votePeopleEachOption);

    }

    @Transactional(readOnly = true)
    public ToktokDto.GetVotePeopleListResponse getVotePeopleList(Long voteOptionId) {
        User visitUser = certifyUser();

        VoteOption voteOption = voteOptionRepository.findById(voteOptionId)
                .orElseThrow(() -> new CustomException(VOTE_OPTION_NOT_FOUND));

        Vote vote = voteOption.getVote();
        // 익명투표일 경우 목록 조회 불가
        if (vote.getIsAnonymous()) {
            throw new CustomException(CANNOT_INQUIRE_VOTE);
        }

        List<UserVote> uservote = userVoteRepository.findByOptionId(voteOptionId);
        if (uservote.size() == 0) {
            return new GetVotePeopleListResponse(null);
        }

        // 투표 참여자 내역(옵션별)
        List<Long> userVoteByOptionId = userVoteRepository.findVotePeopleByOptionId(voteOptionId);

        List<Optional<User>> users = userVoteByOptionId.stream()
            .map(id -> userRepository.findById(id)).collect(Collectors.toList());

        List<ToktokDto.VotePeopleListResponse> response = users.stream()
            .map(uv -> new ToktokDto.VotePeopleListResponse(uv.get(),
                followRepository.countFollowOrNot(visitUser.getId(), uv.get().getId()),visitUser.getId()))
            .collect(Collectors.toList());

        return new ToktokDto.GetVotePeopleListResponse(response);

    }

    public void deletePost(Long toktokId) {
        User user = certifyUser();
        Toktok toktok = findToktok(toktokId);
        if (user.getId() != toktok.getUser().getId()) {
            throw new CustomException(NO_PERMISSION);
        }

        // tag 삭제
        deleteTag(toktokId);

        //link 삭제
        deleteLink(toktokId);

        //image 삭제
        deleteImage(toktokId);

        //좋아요 삭제
        List<Like> likes = likeRepository.findByToktokId(toktokId);
        if (likes != null) {
            likeRepository.deleteAll(likes);
        }

        // 스크랩 삭제
        List<Scrap> scraps = scrapRepository.findByToktokId(toktokId);
        if (scraps != null) {
            scrapRepository.deleteAll(scraps);
        }

        // 댓글 삭제
        List<Comment> comments = commentRepository.findByToktokId(toktokId);
        if (comments != null) {
            // 댓글 신고 내역 삭제
            List<Long> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());
            accuseRepository.deleteByCommentIds(commentIds);
            commentRepository.deleteAll(comments);
        }

        //vote 삭제
        if (toktok.getVote() != null) {
            Vote getVote = toktok.getVote();
            userVoteRepository.deleteByVoteId(getVote.getId());
            voteOptionRepository.deleteByVoteId(getVote.getId());
            toktok.deleteVote();
            entityManager.flush();
            entityManager.clear();
            voteRepository.delete(getVote);
        }

        // 신고내역 삭제
        List<Accuse> accuses = accuseRepository.findByToktokId(toktokId);
        if (accuses != null) {
            accuseRepository.deleteAll(accuses);
        }

        toktokRepository.deleteById(toktokId);
    }

    public void endVote(Long voteId) {
        User user = certifyUser();
        Toktok toktok = toktokRepository.findByVoteId(voteId).get();
        Long writerUserId = toktok.getUser().getId();

        // 게시글 작성자만 투표 종료 가능
        if (user.getId() != writerUserId) {
            throw new CustomException(CANNOT_END_VOTE);
        }

        voteRepository.updateState(voteId);
    }

    @Transactional(readOnly = true)
    public ToktokDto.SearchResponse search(String query, Integer isTag, Integer category, Integer sortBy) {
        List<Toktok> toktoks = toktokRepository.findAllByQuery(query, isTag, category, sortBy);
        return ToktokDto.SearchResponse.from(toktoks);
    }

    public CategoryType viewCategory(int category) {
        CategoryType categoryType = Arrays.stream(CategoryType.values())
            .filter(c -> c.getValue() == category)
            .findAny().get();
        return categoryType;
    }

    private Vote createVote(ToktokDto.PostToktokRequest request) {
        Vote vote = Vote.builder()
            .title(request.getVoteTitle())
            .isSingle(request.getVoteIsSingle())
            .isAnonymous(request.getVoteIsAnonymous())
            .isEnd(false)
            .build();
        return voteRepository.save(vote);
    }

    private void createVoteOption(List<String> voteOptionList, Vote vote) {
        for (String option : voteOptionList) {
            VoteOption voteOption = VoteOption.builder()
                .vote(vote)
                .opt(option)
                .build();
            voteOptionRepository.save(voteOption);
        }
    }


    private void createLink(List<LinkDto.Request> linkList, Toktok toktok) {
        if(linkList != null && !linkList.isEmpty()) {
            for (LinkDto.Request l : linkList) {
                Link link = Link.builder()
                    .toktok(toktok)
                    .url(l.getUrl())
                    .title(l.getTitle())
                    .image(l.getImage())
                    .build();
                linkRepository.save(link);
            }
        }
    }

    private void createTag(List<String> tagList, Toktok toktok) {
        if(tagList != null && !tagList.isEmpty()) {
            for (String tag : tagList) {
                Tag newTag = Tag.builder().toktok(toktok).tag(tag).build();
                tagRepository.save(newTag);
            }
        }
    }

    private void createImages(List<String> images, Toktok toktok) {
        if (images != null && !images.isEmpty()) {
            for (String imgUrl : images) {
                Image uploadImg = Image.builder().toktok(toktok).imageUrl(imgUrl).build();
                imageRepository.save(uploadImg);
            }
        }
    }

    private void deleteTag(Long toktokId) {
        List<Tag> tagList = tagRepository.findByToktokId(toktokId);
        if (tagList != null) {
            tagRepository.deleteAll(tagList);
        }
    }

    private void deleteImage(Long toktokId) {
        List<Image> imageList = imageRepository.findByToktokId(toktokId);
        if (imageList != null) {
            imageRepository.deleteAll(imageList);
        }
    }

    private void deleteLink(Long toktokId) {
        List<Link> linkList = linkRepository.findByToktokId(toktokId);
        if (linkList != null) {
            linkRepository.deleteAll(linkList);
        }
    }

    private User certifyUser() {
        User user = userRepository.findById(jwtService.getId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        return user;
    }

    private Toktok findToktok(Long id) {
        Toktok toktok = toktokRepository.findById(id)
            .orElseThrow(() -> new CustomException(TOKTOKPOST_NOT_FOUND));
        return toktok;
    }
}