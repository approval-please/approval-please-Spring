package com.umc.approval.domain.toktok.service;


import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;

import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like_category.entity.LikeCategory;
import com.umc.approval.domain.like_category.entity.LikeCategoryRepository;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.link.service.LinkService;
import com.umc.approval.domain.tag.service.TagService;
import com.umc.approval.domain.toktok.dto.ToktokRequestDto;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.domain.vote.entity.Vote;
import com.umc.approval.domain.vote.entity.VoteOption;
import com.umc.approval.domain.vote.service.VoteOptionService;
import com.umc.approval.domain.vote.service.VoteService;
import com.umc.approval.global.aws.service.AwsS3Service;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.CategoryType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.swing.plaf.synth.SynthTextAreaUI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequiredArgsConstructor
@Service
public class ToktokService {

    @Autowired
    private final AwsS3Service awsS3Service;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final VoteService voteService;

    @Autowired
    private final VoteOptionService voteOptionService;

    @Autowired
    private final LinkService linkService;

    @Autowired
    private final TagService tagService;

    @Autowired
    private final ToktokRepository toktokRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ImageRepository imageRepository;

    @Autowired
    private final LikeCategoryRepository likeCategoryRepository;

    private Long voteId;
    private Vote vote;
    private Toktok toktok;

    public void createPost(ToktokRequestDto toktokRequestDto, List<MultipartFile> files) {
        //User 정보 가져오기
        User user = userRepository.findById(jwtService.getId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        //투표 등록
        if (toktokRequestDto.getVoteTitle() != null) {
            vote = Vote.builder()
                .title(toktokRequestDto.getVoteTitle())
                .isSingle(toktokRequestDto.getIsSingle())
                .isAnonymous(toktokRequestDto.getIsAnonymous())
                .isEnd(false)
                .build();
            voteService.createVote(vote);

            //투표 선택지 저장
            for (String option : toktokRequestDto.getOption()) {
                VoteOption voteOption = VoteOption.builder()
                    .vote(vote)
                    .opt(option)
                    .build();
                voteOptionService.createVoteOption(voteOption);
            }
        }

        //카테고리 등록
        CategoryType categoryType = viewCategory(toktokRequestDto.getCategory());

        //결제톡톡 게시글 등록
        toktok = Toktok.builder()
            .user(user)
            .content(toktokRequestDto.getContent())
            .category(categoryType)
            .vote(vote)
            .view((long) 0)
            .notification(true)
            .build();

        toktokRepository.save(toktok);

        //링크 등록
        if (toktokRequestDto.getLinkUrl() != null) {
            List<String> linkList = toktokRequestDto.getLinkUrl();
            linkService.createLink(linkList, toktok);
        }

        //태그 등록
        if (toktokRequestDto.getTag() != null) {
            List<String> tagList = toktokRequestDto.getTag();
            tagService.createTag(tagList, toktok);
        }

        //aws 이미지 저장
        if (files.size() == 0) {
            String imageUrl = awsS3Service.uploadImage(files.get(0));
            Image images = Image.builder()
                .toktok(toktok)
                .imageUrl(imageUrl)
                .build();
            imageRepository.save(images);

        } else {
            List<String> imageUrl = awsS3Service.uploadImage(files);
            for (String image : imageUrl) {
                Image images = Image.builder()
                    .toktok(toktok)
                    .imageUrl(image)
                    .build();
                imageRepository.save(images);
            }
        }

    }
    public CategoryType viewCategory(int category) {
        CategoryType categoryType = Arrays.stream(CategoryType.values())
            .filter(c -> c.getValue() == category)
            .findAny().get();
        return categoryType;
    }
}
