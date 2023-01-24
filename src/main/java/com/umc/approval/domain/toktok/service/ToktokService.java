package com.umc.approval.domain.toktok.service;


import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.link.dto.LinkDto;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.domain.vote.entity.Vote;
import com.umc.approval.domain.vote.entity.VoteOption;
import com.umc.approval.domain.vote.entity.VoteOptionRepository;
import com.umc.approval.domain.vote.entity.VoteRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

        // 링크 등록
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

        //vote 삭제
        if (toktok.getVote() != null) {
            Optional<Vote> vote = voteRepository.findById(toktok.getVote().getId());
            Vote getVote = vote.get();
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
