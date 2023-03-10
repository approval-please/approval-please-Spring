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

        //?????? ??????
        Vote vote = null;
        if (request.getVoteTitle() != null) {
            vote = createVote(request);
            createVoteOption(request.getVoteOption(), vote);
        }

        //???????????? ??????
        CategoryType categoryType = viewCategory(request.getCategory());

        //???????????? ????????? ??????
        Toktok toktok = request.toEntity(user, categoryType, vote);
        toktokRepository.save(toktok);

        //?????? ??????
        createLink(request.getLink(), toktok);

        //?????? ??????
        createTag(request.getTag(), toktok);

        //????????? ??????
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

        // ???????????? ??????
        Toktok toktok = findToktok(toktokId);

        List<String> tags = tagRepository.findTagNameListByToktokId(toktokId);
        List<String> images = imageRepository.findImageUrlListBytoktokId(toktokId);
        List<Link> reportLinkList = linkRepository.findByToktokId(toktokId);
        List<LinkDto.Response> linkResponse = reportLinkList.stream().map(LinkDto.Response::fromEntity).collect(Collectors.toList());
        Boolean isModified = true;
        // ???????????? ????????? ?????? ?????? ??????
        if (toktok.getCreatedAt() == toktok.getModifiedAt()) {
            isModified = false;
        }

        // ????????????(?????????, ????????? ??????)
        Long userId = jwtService.getIdDirectHeader(request);
        User visitUser = null;
        if (userId != null) {
            visitUser = userRepository.findById(userId).get();
        }
        User writer = toktok.getUser();

        // ?????? ??????
        Vote vote = toktok.getVote();
        List<ToktokDto.VoteOptionResponse> voteOption = null;
        List<ToktokDto.VoteOptionResponse> voteSelect = null;
        List<Long> votePeopleEachOption = null;
        Integer votePeople = null;
        if (vote != null) {
            voteOption = vote.getVoteOptions().stream()
                .map(ToktokDto.VoteOptionResponse::fromEntity)
                .collect(Collectors.toList());
            votePeople = userVoteRepository.findVotePeopleCount(vote.getId());  // ?????? ??? ????????? ???
        }

        // ?????????, ?????????, ?????? ???
        Long likedCount = likeRepository.countByToktok(toktok);
        Long commentCount = commentRepository.countByToktokId(toktok.getId());
        Long scrapCount = scrapRepository.countByToktok(toktok);
        Boolean likeOrNot = true;
        Boolean followOrNot = true;
        Boolean scrapOrNot = true;
        Boolean writerOrNot = false;

        // ?????? ????????? ????????? ????????? ??????
        scrapOrNot = scrapRepository.countByUserAndToktok(visitUser, toktok) == 0 ? false : true;

        if (userId != null) {
            //????????? ??? ?????????
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

        // ????????? ????????? ????????? ????????? ??????????????? ??????
        if (userId == writer.getId()) {
            writerOrNot = true;
            voteSelect = null;
        }

        // ?????? ????????? ???????????? ???????????? ??????
        likeOrNot = likeRepository.countByUserAndToktok(visitUser, toktok) == 0 ? false : true;

        // ????????? ?????? ????????? ??? ????????? ?????? ??? ????????? ????????? ????????? ??????
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

        // ?????? ??????
        deleteTag(id);
        createTag(request.getTag(), toktok);

        // ?????? ??????
        deleteLink(id);
        createLink(request.getLink(), toktok);

        CategoryType categoryType = viewCategory(request.getCategory());

        // ????????? ????????? ?????? ?????? ?????? ????????? ??????????????? ??????
        if (toktok.getVote() != null && toktok.getVote().getIsEnd().equals(true) && (request.getVoteTitle() != null
            || request.getVoteOption() != null ||
            request.getVoteIsSingle() != null || request.getVoteIsAnonymous() != null)) {
            throw new CustomException(VOTE_IS_END);
        }

        // ????????? ????????? ?????? ???????????? ??????
        if (toktok.getVote() == null && request.getVoteTitle() != null) {
            Vote vote = createVote(request);
            createVoteOption(request.getVoteOption(), vote);
            toktok.update(request, categoryType, vote);
        } else if (toktok.getVote() != null) {
            Vote getVote = toktok.getVote();
            userVoteRepository.deleteByVoteId(getVote.getId());
            voteOptionRepository.deleteByVoteId(getVote.getId());
            // ????????? ????????? ????????? ??????
            if (getVote.getTitle() != null && request.getVoteTitle() == null) {
                toktok.deleteVote();
                entityManager.flush();
                entityManager.clear();
                voteRepository.delete(getVote);
            } else if (toktok.getVote() != null && request.getVoteTitle() != null) {
                // ????????? ????????? ???????????? ??????
                getVote.update(request);
                createVoteOption(request.getVoteOption(), getVote);
            }
            toktok.update(request, categoryType, getVote);
        } else {
            // ?????? ????????? ?????? ????????? ?????? ??????
            toktok.update(request, categoryType, null);
        }

        // ????????? ??????
        deleteImage(id);
        createImages(request.getImages(), toktok);
    }

    // ????????????
    public ToktokDto.VotePeopleEachOptionResponse vote(ToktokDto.VoteRequest request, Long voteId) {
        User user = certifyUser();
        List<Long> voteOptionIds = request.getVoteOptionIds();
        Vote vote = voteRepository.findById(voteId).get();
        List<Long> findOptions = vote.getVoteOptions().stream()
            .map(VoteOption::getId).collect(Collectors.toList());

        // ?????? ????????? ?????? ???????????? ?????? ???????????? ??????
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

        // ??? ??????????????? ????????? ?????? ???
        List<Long> votePeopleEachOption = findOptions.stream().map(id ->
            vote.getUserVotes().stream()
                .filter(uv ->
                    uv.getVoteOption().getId() == id)
                .count())
            .collect(Collectors.toList());

        return new ToktokDto.VotePeopleEachOptionResponse(votePeopleEachOption);

    }

    public ToktokDto.GetVotePeopleListResponse getVotePeopleList(Long voteOptionId) {
        User visitUser = certifyUser();

        VoteOption voteOption = voteOptionRepository.findById(voteOptionId)
                .orElseThrow(() -> new CustomException(VOTE_OPTION_NOT_FOUND));

        Vote vote = voteOption.getVote();
        // ??????????????? ?????? ?????? ?????? ??????
        if (vote.getIsAnonymous()) {
            throw new CustomException(CANNOT_INQUIRE_VOTE);
        }

        List<UserVote> uservote = userVoteRepository.findByOptionId(voteOptionId);
        if (uservote.size() == 0) {
            return new GetVotePeopleListResponse(null);
        }

        // ?????? ????????? ??????(?????????)
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

        // tag ??????
        deleteTag(toktokId);

        //link ??????
        deleteLink(toktokId);

        //image ??????
        deleteImage(toktokId);

        //????????? ??????
        List<Like> likes = likeRepository.findByToktokId(toktokId);
        if (likes != null) {
            likeRepository.deleteAll(likes);
        }

        // ????????? ??????
        List<Scrap> scraps = scrapRepository.findByToktokId(toktokId);
        if (scraps != null) {
            scrapRepository.deleteAll(scraps);
        }

        // ?????? ??????
        List<Comment> comments = commentRepository.findByToktokId(toktokId);
        if (comments != null) {
            // ?????? ?????? ?????? ??????
            List<Long> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());
            accuseRepository.deleteByCommentIds(commentIds);
            commentRepository.deleteAll(comments);
        }

        //vote ??????
        if (toktok.getVote() != null) {
            Vote getVote = toktok.getVote();
            userVoteRepository.deleteByVoteId(getVote.getId());
            voteOptionRepository.deleteByVoteId(getVote.getId());
            toktok.deleteVote();
            entityManager.flush();
            entityManager.clear();
            voteRepository.delete(getVote);
        }

        // ???????????? ??????
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

        // ????????? ???????????? ?????? ?????? ??????
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