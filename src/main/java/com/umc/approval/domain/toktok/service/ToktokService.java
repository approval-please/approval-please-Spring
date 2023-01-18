package com.umc.approval.domain.toktok.service;


import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;

import com.umc.approval.domain.image.service.ImageService;
import com.umc.approval.domain.link.service.LinkService;
import com.umc.approval.domain.tag.service.TagService;
import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.domain.vote.entity.Vote;
import com.umc.approval.domain.vote.entity.VoteOption;
import com.umc.approval.domain.vote.service.VoteOptionService;
import com.umc.approval.domain.vote.service.VoteService;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.CategoryType;
import java.util.Arrays;
import java.util.List;
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
    private final ImageService imageService;

    @Autowired
    private final ToktokRepository toktokRepository;

    @Autowired
    private final UserRepository userRepository;

    private Vote vote;
    private Toktok toktok;

    public void createPost(ToktokDto.PostToktokRequest request, List<MultipartFile> files) {

        User user = userRepository.findById(jwtService.getId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        //투표 등록
        if (request.getVoteTitle() != null) {
            vote = Vote.builder()
                .title(request.getVoteTitle())
                .isSingle(request.getVoteIsSingle())
                .isAnonymous(request.getVoteIsAnonymous())
                .isEnd(false)
                .build();
            voteService.createVote(vote);

            //투표 선택지 저장
            for (String option : request.getVoteOption()) {
                VoteOption voteOption = VoteOption.builder()
                    .vote(vote)
                    .opt(option)
                    .build();
                voteOptionService.createVoteOption(voteOption);
            }
        }

        //카테고리 등록
        CategoryType categoryType = viewCategory(request.getCategory());

        //결제톡톡 게시글 등록
        toktok = Toktok.builder()
            .user(user)
            .content(request.getContent())
            .category(categoryType)
            .vote(vote)
            .view((long) 0)
            .notification(true)
            .build();

        toktokRepository.save(toktok);

        //링크 등록
        if (request.getLinkUrl() != null) {
            List<String> linkList = request.getLinkUrl();
            linkService.createLink(linkList, toktok);
        }

        //태그 등록
        if (request.getTag() != null) {
            List<String> tagList = request.getTag();
            tagService.createTag(tagList, toktok);
        }

        //aws 이미지 저장
        if (files.size() == 1) {
            imageService.createImage(files.get(0), toktok);

        } else {
            imageService.createImage(files, toktok);
        }
    }

    public CategoryType viewCategory(int category) {
        CategoryType categoryType = Arrays.stream(CategoryType.values())
            .filter(c -> c.getValue() == category)
            .findAny().get();
        return categoryType;
    }
}
