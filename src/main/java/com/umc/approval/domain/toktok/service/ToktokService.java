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

    public void createPost(ToktokDto.PostToktokRequest request, List<MultipartFile> files) {

        User user = userRepository.findById(jwtService.getId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        //투표 등록
        Vote vote = null;
        if (request.getVoteTitle() != null) {
            vote = Vote.builder()
                .title(request.getVoteTitle())
                .isSingle(request.getVoteIsSingle())
                .isAnonymous(request.getVoteIsAnonymous())
                .isEnd(false)
                .build();
            voteRepository.save(vote);
            for (String option : request.getVoteOption()) {
                VoteOption voteOption = VoteOption.builder()
                    .vote(vote)
                    .opt(option)
                    .build();
                voteOptionRepository.save(voteOption);
            }
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
            for (String link : linkList) {
                Link newLink = Link.builder().toktok(toktok).linkUrl(link).build();
                linkRepository.save(newLink);
            }
        }

        //태그 등록
        if (request.getTag() != null) {
            List<String> tagList = request.getTag();
            for (String tag : tagList) {
                Tag newTag = Tag.builder().toktok(toktok).tag(tag).build();
                tagRepository.save(newTag);
            }
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

    public void updatePost(@PathVariable Long id, ToktokDto.PostToktokRequest request,
        List<MultipartFile> files) {
        Toktok toktok = toktokRepository.findById(id)
            .orElseThrow(() -> new CustomException(TOKTOKPOST_NOT_FOUND));

        Vote vote = voteRepository.findById(toktok.getVote().getId()).get();
        // 투표 종료 된 글은 투표 관련 사항 수정 불가
        if (vote.getIsEnd().equals(true) && (request.getVoteTitle() != null
            || request.getVoteOption() != null ||
            request.getVoteIsAnonymous() != null || request.getVoteIsSingle() != null)) {
            throw new CustomException(VOTE_IS_END);
        }


        List<VoteOption> voteOption = voteOptionRepository.findByVote(vote);
        if (request.getVoteTitle() != null) {
            vote.update(request);
            voteOptionRepository.deleteAll(voteOption);

//            for (String option : request.getVoteOption()) {
//                VoteOption vote_option = VoteOption.builder()
//                    .vote(vote)
//                    .opt(option)
//                    .build();
//                voteOptionRepository.save(vote_option);
//            }
        }

        // 태그 수정
        if (request.getTag() != null) {
            List<Tag> tags = tagRepository.findByToktok(toktok);
            tagRepository.deleteAll(tags);
            List<String> tagList = request.getTag();
            for (String tag : tagList) {
                Tag newTag = Tag.builder().toktok(toktok).tag(tag).build();
                tagRepository.save(newTag);
            }
        }



        CategoryType categoryType = viewCategory(request.getCategory());
        //톡톡 게시글 수정
//        toktok.update(request, categoryType, vote);

        //이미지 수정
//        if (files.size() == 1) {
//            String imgUrl = awsS3Service.uploadImage(files.get(0));
//            List<Image> uploadImg = imageRepository.findByToktok(toktok);
//            uploadImg.get(0).update(imgUrl);
//        }
//        else {
//            List<String> imgUrls = awsS3Service.uploadImage(files);
//            int i = 0;
//            List<Image> uploadImg = imageRepository.findByToktok(toktok);
//            for (Image image : uploadImg) {
//                image.update(imgUrls.get(i));
//                i += 1;
//            }
//            //이미지 추가하는 경우
//        }
    }

    public CategoryType viewCategory(int category) {
        CategoryType categoryType = Arrays.stream(CategoryType.values())
            .filter(c -> c.getValue() == category)
            .findAny().get();
        return categoryType;
    }
}
