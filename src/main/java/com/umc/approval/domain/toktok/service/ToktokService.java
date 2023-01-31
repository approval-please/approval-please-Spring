package com.umc.approval.domain.toktok.service;


import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.comment.entity.CommentRepository;
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
        Toktok toktok = Toktok.builder()
                .user(user)
                .content(request.getContent())
                .category(categoryType)
                .vote(vote)
                .view(0L)
                .notification(true)
                .build();

        toktokRepository.save(toktok);

        //링크 등록
        if (request.getLink() != null && !request.getLink().isEmpty()) {
            List<LinkDto.Request> linkList = request.getLink();
            createLink(linkList, toktok);

        }

        //태그 등록
        if (request.getTag() != null) {
            List<String> tagList = request.getTag();
            createTag(tagList, toktok);
        }

        //이미지 등록
        if (request.getImages() != null) {
            for (String imgUrl : request.getImages()) {
                Image uploadImg = Image.builder().toktok(toktok).imageUrl(imgUrl).build();
                imageRepository.save(uploadImg);
            }
        }
    }

    public ToktokDto.GetToktokResponse getToktok(Long toktokId, HttpServletRequest request) {
        toktokRepository.updateView(toktokId);

        // 결재톡톡 정보
        Toktok toktok = findToktok(toktokId);

        List<String> tags = tagRepository.findTagNameListByToktokId(toktokId);
        List<String> images = imageRepository.findImageUrlListBytoktokId(toktokId);
        List<Link> reportLinkList = linkRepository.findByToktokId(toktokId);
        List<LinkDto.Response> linkResponse;
        linkResponse = reportLinkList.stream().map(LinkDto.Response::fromEntity).collect(Collectors.toList());
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
            votePeople = userVoteRepository.findVotePeople(vote.getId());  // 투표 총 참여자 수
        }

        // 좋아요, 스크랩, 댓글 수
        Long likedCount = likeRepository.countByToktok(toktok);
        Long commentCount = commentRepository.countByToktokId(toktok.getId());
        Long scrapCount = scrapRepository.countByToktok(toktok);
        Long likeToktokOrNot = likeRepository.countByUserAndToktok(visitUser, toktok);
        Long scrapToktokOrNot = scrapRepository.countByUserAndToktok(visitUser, toktok);
        Boolean likeOrNot = true;
        Boolean followOrNot = true;
        Boolean scrapOrNot = true;

        // 해당 유저가 스크랩 했는지 여부
        if(scrapToktokOrNot == 0) {
            scrapOrNot = false;
        }

        if (userId != null) {
            //로그인 한 사용자
            User user = userRepository.findById(userId).get();
            if (vote != null) {
                voteSelect = userVoteRepository.findAllByUserAndVote(user.getId(), toktok.getId())
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
        Boolean writerOrNot = false;
        if (userId == writer.getId()) {
            writerOrNot = true;
            voteSelect = null;
        }

        // 해당 유저가 게시글을 눌렀는지 여부
        if (likeToktokOrNot == 0) {
            likeOrNot = false;
        }

        // 게시글 상세 조회를 한 유저가 글을 쓴 유저를 팔로우 했는지 여부
        Long from_userId = userId;
        Long to_userId = toktok.getUser().getId();
        Integer follow = followRepository.countFollowOrNot(from_userId, to_userId);
        if (from_userId == to_userId) {
            followOrNot = null;
        } else if (follow == 0) {
            followOrNot = false;
        }
        if (userId == toktok.getUser().getId()) {
            voteSelect = null;
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
        List<Tag> tags = tagRepository.findByToktokId(toktok.getId());
        if (tags != null && !tags.isEmpty()) {
            tagRepository.deleteAll(tags);
        }
        if (request.getTag() != null) {
            List<String> tagList = request.getTag();
            if (tagList != null && !tagList.isEmpty()) {
                createTag(tagList, toktok);
            }
        }

        // 링크 수정
        List<Link> links = linkRepository.findByToktokId(toktok.getId());
        if (links != null && !links.isEmpty()) {
            linkRepository.deleteAll(links);
        }
        if (request.getLink() != null && !request.getLink().isEmpty()) {
            List<LinkDto.Request> linkList = request.getLink();
            createLink(linkList, toktok);
        }

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
            Optional<Vote> vote = voteRepository.findById(toktok.getVote().getId());
            Vote getVote = vote.get();
            List<VoteOption> voteOption = voteOptionRepository.findByVote(getVote);
            List<UserVote> userVotes = userVoteRepository.findByVote(getVote);
            userVoteRepository.deleteAll(userVotes);
            voteOptionRepository.deleteAll(voteOption);
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
        List<Image> images = imageRepository.findByToktokId(toktok.getId());
        if (images != null && !images.isEmpty()) {
            imageRepository.deleteAll(images);
        }
        if (request.getImages() != null) {
            for (String imgUrl : request.getImages()) {
                Image uploadImg = Image.builder().toktok(toktok).imageUrl(imgUrl).build();
                imageRepository.save(uploadImg);
            }
        }
    }

    // 투표하기
    public ToktokDto.VotePeopleEachOptionResponse getVotePeople(ToktokDto.VoteRequest request, Long voteId) {
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

    public void deletePost(Long toktokId) {
        User user = certifyUser();
        Toktok toktok = findToktok(toktokId);
        if (user.getId() != toktok.getUser().getId()) {
            throw new CustomException(NO_PERMISSION);
        }

        // tag 삭제
        List<Tag> tagList = tagRepository.findByToktokId(toktokId);
        if (tagList != null) {
            tagRepository.deleteAll(tagList);
        }

        //link 삭제
        List<Link> linkList = linkRepository.findByToktokId(toktokId);
        if (linkList != null) {
            linkRepository.deleteAll(linkList);
        }

        //image 삭제
        List<Image> imageList = imageRepository.findByToktokId(toktokId);
        if (imageList != null) {
            imageRepository.deleteAll(imageList);
        }

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
            commentRepository.deleteAll(comments);
        }

        //vote 삭제
        if (toktok.getVote() != null) {
            Optional<Vote> vote = voteRepository.findById(toktok.getVote().getId());
            Vote getVote = vote.get();
            userVoteRepository.deleteByVoteId(getVote.getId());
            List<VoteOption> voteOptionList = voteOptionRepository.findByVote(getVote);
            if (voteOptionList != null) {
                voteOptionRepository.deleteAll(voteOptionList);
            }
            toktok.deleteVote();
            entityManager.flush();
            entityManager.clear();
            voteRepository.delete(getVote);
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

    public Vote createVote(ToktokDto.PostToktokRequest request) {
        Vote vote = Vote.builder()
                .title(request.getVoteTitle())
                .isSingle(request.getVoteIsSingle())
                .isAnonymous(request.getVoteIsAnonymous())
                .isEnd(false)
                .build();
        return voteRepository.save(vote);
    }

    public void createVoteOption(List<String> voteOptionList, Vote vote) {
        for (String option : voteOptionList) {
            VoteOption voteOption = VoteOption.builder()
                    .vote(vote)
                    .opt(option)
                    .build();
            voteOptionRepository.save(voteOption);
        }
    }


    public void createLink(List<LinkDto.Request> linkList, Toktok toktok) {
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

    public void createTag(List<String> tagList, Toktok toktok) {
        for (String tag : tagList) {
            Tag newTag = Tag.builder().toktok(toktok).tag(tag).build();
            tagRepository.save(newTag);
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
