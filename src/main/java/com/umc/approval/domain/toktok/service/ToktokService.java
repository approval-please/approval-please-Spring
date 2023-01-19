package com.umc.approval.domain.toktok.service;


import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
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
import com.umc.approval.global.aws.service.AwsS3Service;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.CategoryType;
import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static com.umc.approval.global.exception.CustomErrorType.TOKTOKPOST_NOT_FOUND;
import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;
import static com.umc.approval.global.exception.CustomErrorType.VOTE_IS_END;

@Transactional
@RequiredArgsConstructor
@Service
public class ToktokService {

    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;
    private final ToktokRepository toktokRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final LinkRepository linkRepository;
    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;
    private final EntityManager entityManager;

    public void createPost(ToktokDto.PostToktokRequest request, List<MultipartFile> files) {

        User user = userRepository.findById(jwtService.getId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

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
        if (request.getLinkUrl() != null) {
            List<String> linkList = request.getLinkUrl();
            createLink(linkList, toktok);
        }

        //태그 등록
        if (request.getTag() != null) {
            List<String> tagList = request.getTag();
            createTag(tagList, toktok);
        }

        //aws 이미지 저장
        if (files.size() == 1) {
            String imgUrl = awsS3Service.uploadImage(files.get(0));
            Image uploadImg = Image.builder().toktok(toktok).imageUrl(imgUrl).build();
            imageRepository.save(uploadImg);

        } else {
            List<String> imgUrls = awsS3Service.uploadImage(files);
            for (String imgUrl : imgUrls) {
                Image uploadImg = Image.builder().toktok(toktok).imageUrl(imgUrl).build();
                imageRepository.save(uploadImg);
            }
        }
    }

    public void updatePost(Long id, ToktokDto.PostToktokRequest request,
        List<MultipartFile> files) {
        Toktok toktok = toktokRepository.findById(id)
            .orElseThrow(() -> new CustomException(TOKTOKPOST_NOT_FOUND));

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
        if (request.getLinkUrl() != null) {
            List<String> linkList = request.getLinkUrl();
            if (linkList != null && !linkList.isEmpty()) {
                createLink(linkList, toktok);
            }
        }

        CategoryType categoryType = viewCategory(request.getCategory());

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

        //이미지 수정
        List<Image> images = imageRepository.findByToktokId(toktok.getId());
        if (images != null && !images.isEmpty()) {
            imageRepository.deleteAll(images);
        }

        if (files != null && files.size() == 1) {
            String imgUrl = awsS3Service.uploadImage(files.get(0));
            Image uploadImg = Image.builder().toktok(toktok).imageUrl(imgUrl).build();
            imageRepository.save(uploadImg);

        } else {
            if (files != null && !files.isEmpty()) {
                List<String> imgUrls = awsS3Service.uploadImage(files);
                for (String imgUrl : imgUrls) {
                    Image uploadImg = Image.builder().toktok(toktok).imageUrl(imgUrl).build();
                    imageRepository.save(uploadImg);
                }
            }
        }
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

    public void createLink(List<String> linkList, Toktok toktok) {
        for (String link : linkList) {
            Link newLink = Link.builder().toktok(toktok).linkUrl(link).build();
            linkRepository.save(newLink);
        }
    }

    public void createTag(List<String> tagList, Toktok toktok) {
        for (String tag : tagList) {
            Tag newTag = Tag.builder().toktok(toktok).tag(tag).build();
            tagRepository.save(newTag);
        }
    }
}
