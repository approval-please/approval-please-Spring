package com.umc.approval.domain.toktok.dto;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.vote.entity.Vote;
import com.umc.approval.global.util.DateUtil;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.link.dto.LinkDto;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.vote.entity.Vote;
import com.umc.approval.domain.vote.entity.VoteOption;
import com.umc.approval.global.util.DateUtil;
import lombok.*;

import javax.validation.constraints.*;
import java.util.List;
import java.util.stream.Collectors;


public class ToktokDto {

    @Data
    public static class PostToktokRequest {

        @NotNull(message = "카테고리는 필수 입력값입니다.")
        @Min(value = 0, message = "카테고리는 0부터 17까지의 정수 값입니다.")
        @Max(value = 17, message = "카테고리는 0부터 17까지의 정수 값입니다.")
        private Integer category;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        private String voteTitle;

        private Boolean voteIsSingle;

        private Boolean voteIsAnonymous;

        private Boolean voteIsEnd;

        @Size(min = 2, max = 4, message = "투표 선택지는 2~4개까지 가능합니다")
        private List<String> voteOption;

        @Size(max = 4, message = "링크 첨부는 최대 4개까지 가능합니다")
        private List<LinkDto.Request> link;

        @Size(max = 4, message = "태그 첨부는 최대 4개까지 가능합니다")
        private List<String> tag;

        private List<String> images;
    }

    //게시글 상세 조회
    @Data
    public static class GetToktokResponse {

        // user
        private Long userId;
        private String profileImage;
        private String nickname;
        private Integer level;

        // toktok
        private Long toktokId;
        private Integer category;
        private String content;
        private List<LinkDto.Response> link;
        private List<String> tag;
        private List<String> images;

        // vote
        private Long voteId;
        private String voteTitle;
        private Boolean voteIsEnd;
        private Integer votePeople;  // 투표 총 참여자 수
        private Boolean voteIsSingle;
        private Boolean voteIsAnonymous;
        private List<VoteOptionResponse> voteOption;
        private List<VoteOptionResponse> voteSelect;
        private List<Long> votePeopleEachOption;

        // etc
        private Boolean writerOrNot;  // 작성자인지 여부
        private Long likedCount;
        private Boolean likeOrNot;
        private Boolean followOrNot;
        private Boolean scrapOrNot;
        private Long scrapCount;
        private Long commentCount;
        private Boolean isNotification;
        private Boolean isModified;
        private String datetime;
        private Long view;

        public GetToktokResponse(User user, Toktok toktok, Vote vote, List<String> tags,
            List<String> images, List<LinkDto.Response> linkResponse, Long likedCount,
            Long commentCount, Long scrapCount, Boolean likeOrNot,
            Boolean followOrNot, List<VoteOptionResponse> voteOption, List<VoteOptionResponse> voteSelect,
            Integer votePeople, List<Long> votePeopleEachOption, Boolean writerOrNot, Boolean isModified, Boolean scrapOrNot) {
            this.userId = user.getId();
            this.profileImage = user.getProfileImage();
            this.level = user.getLevel();
            this.nickname = user.getNickname();

            this.toktokId = toktok.getId();
            this.category = toktok.getCategory().getValue();
            this.content = toktok.getContent();
            this.link = linkResponse;
            this.tag = tags;
            this.images = images;

            this.voteId = vote == null ? null : toktok.getVote().getId();
            this.voteTitle = vote == null ? null : vote.getTitle();
            this.voteIsEnd = vote == null ? null : vote.getIsEnd();
            this.votePeople = votePeople;
            this.voteIsSingle = vote == null ? null : vote.getIsSingle();
            this.voteIsAnonymous = vote == null ? null : vote.getIsAnonymous();
            this.voteOption = voteOption;
            this.voteSelect = voteSelect;
            this.votePeopleEachOption = votePeopleEachOption;

            this.writerOrNot = writerOrNot;
            this.likedCount = likedCount;
            this.likeOrNot = likeOrNot;
            this.followOrNot = followOrNot;
            this.scrapCount = scrapCount;
            this.commentCount = commentCount;
            this.isNotification = toktok.isNotification();
            this.datetime = DateUtil.convert(toktok.getCreatedAt());
            this.view = toktok.getView();
            this.isModified = isModified;
            this.scrapOrNot = scrapOrNot;
        }
    }

    @Data
    public static class GetVotePeopleListResponse {
        private List<ToktokDto.VotePeopleListResponse> content;

        public GetVotePeopleListResponse(List<ToktokDto.VotePeopleListResponse> content) {
            this.content = content;
        }
    }

    @Data
    public static class VotePeopleListResponse {
        private String profileImage;
        private String nickname;
        private Integer level;
        private Boolean followOrNot;

        public VotePeopleListResponse(User user, Integer followOrNot) {
            this.profileImage = user.getProfileImage();
            this.nickname = user.getNickname();
            this.level = user.getLevel();
            this.followOrNot = followOrNot == 0? false : true;
            System.out.println(followOrNot);
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchResponse {
        private Integer toktokCount;
        private List<SearchListResponse> content;

        public static SearchResponse from(List<Toktok> toktoks) {
            return SearchResponse.builder()
                    .toktokCount(toktoks.size())
                    .content(toktoks.stream().map(SearchListResponse::fromEntity).collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchListResponse {
        private Long toktokId;
        private Integer category;
        private Long userId;
        private String nickname;
        private Integer userLevel;
        private String profileImage;
        private Integer likeCount;
        private Integer commentCount;
        private Long view;
        private String content;
        private List<String> tag;
        private List<String> images;
        private LinkDto.Response link;
        private String datetime;
        private VoteResponse vote;

        public static SearchListResponse fromEntity(Toktok toktok) {
            return SearchListResponse.builder()
                    .toktokId(toktok.getId())
                    .category(toktok.getCategory().getValue())
                    .userId(toktok.getUser().getId())
                    .nickname(toktok.getUser().getNickname())
                    .userLevel(toktok.getUser().getLevel())
                    .profileImage(toktok.getUser().getProfileImage())
                    .likeCount(toktok.getLikes() == null ? 0 : toktok.getLikes().size())
                    .commentCount(toktok.getComments() == null ? 0 : toktok.getComments().size())
                    .view(toktok.getView())
                    .content(toktok.getContent())
                    .tag(toktok.getTags() == null ? null :
                            toktok.getTags().stream().map(Tag::getTag).collect(Collectors.toList()))
                    .images(toktok.getImages() == null ? null :
                            toktok.getImages().stream().map(Image::getImageUrl).collect(Collectors.toList()))
                    .link((toktok.getLinks() == null || toktok.getLinks().isEmpty()) ? null :
                            LinkDto.Response.fromEntity(toktok.getLinks().get(0)))
                    .datetime(DateUtil.convert(toktok.getCreatedAt()))
                    .vote(toktok.getVote() == null ? null : VoteResponse.fromEntity(toktok.getVote()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VoteResponse {
        private Long voteId;
        private String title;
        private Boolean isEnd;
        private Boolean isSingle;
        private Boolean isAnonymous;
        private Integer voteUserCount;

        public static VoteResponse fromEntity(Vote vote) {
            return VoteResponse.builder()
                    .voteId(vote.getId())
                    .title(vote.getTitle())
                    .isEnd(vote.getIsEnd())
                    .isSingle(vote.getIsSingle())
                    .isAnonymous(vote.getIsAnonymous())
                    .voteUserCount(vote.getUserVotes() == null ? 0 : vote.getUserVotes().size())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VoteOptionResponse {
        private Long voteOptionId;
        private String opt;

        public static VoteOptionResponse fromEntity(VoteOption voteOption) {
            return VoteOptionResponse.builder()
                    .voteOptionId(voteOption.getId())
                    .opt(voteOption.getOpt())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    public static class VoteRequest {
        @Size(min = 1, max = 4, message = "투표는 1~4개까지 선택 가능합니다")
        private List<Long> voteOptionIds;
    }

    @Data
    public static class VotePeopleEachOptionResponse {
        private List<Long> votePeoepleEachOption;

        public VotePeopleEachOptionResponse(List<Long> votePeoepleEachOption) {
            this.votePeoepleEachOption = votePeoepleEachOption;
        }
    }
}
