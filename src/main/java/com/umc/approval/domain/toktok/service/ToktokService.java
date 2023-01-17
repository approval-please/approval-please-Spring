package com.umc.approval.domain.toktok.service;


import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;

import com.umc.approval.domain.category.entity.Category;
import com.umc.approval.domain.category.entity.CategoryRepository;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.link.entity.Link;
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
    private final ToktokRepository toktokRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ImageRepository imageRepository;

    @Autowired
    private final CategoryRepository categoryRepository;

    private Long voteId;
    private Vote vote;
    private Category category;
    private Toktok toktok;


    public void createPost(ToktokRequestDto toktokRequestDto, List<MultipartFile> files) {
//        User user = userRepository.findById(jwtService.getId())
//            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        System.out.println(jwtService.getId());
//        Optional<User> user = userRepository.findByEmail("sju08227@naver.com");
        //투표 등록했는지 확인
        if (toktokRequestDto.getVoteTitle() != null) {
            vote = Vote.builder()
                .title(toktokRequestDto.getVoteTitle())
                .isSingle(toktokRequestDto.getIsSingle())
                .isAnonymous(toktokRequestDto.getIsAnonymous())
                .isEnd(false)
                .build();
            voteService.createVote(vote);

            for (String option : toktokRequestDto.getOption()) {
                VoteOption voteOption = VoteOption.builder()
                    .vote(vote)
                    .opt(option)
                    .build();
                voteOptionService.createVoteOption(voteOption);
            }
        }
        //링크 첨부했는지 확인
//        if (toktokRequestDto.getLinkUrl().isEmpty() == false) {
//            List<String> linkList = toktokRequestDto.getLinkUrl();
//            for (String linkUrl : linkList) {
//            }
//        }
//        category = Category.builder()
//            .user(user)
//            .category("디지털 기기")
//            .build();
//        categoryRepository.save(category);
//
////
//        toktok = Toktok.builder()
//            .user(user)
//            .content(toktokRequestDto.getContent())
//            .category(category)
//            .vote(vote)
//            .view((long) 0)
//            .notification(false)
//            .build();
//
//        toktokRepository.save(toktok);

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
                    .toktok(null)
                    .imageUrl(image)
                    .build();
                imageRepository.save(images);
            }
        }

    }
}
