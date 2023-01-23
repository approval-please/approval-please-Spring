package com.umc.approval.domain.like.service;

import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomErrorType;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;

@Transactional
@RequiredArgsConstructor
@Service
public class LikeService {

    private final JwtService jwtService;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public LikeDto.ListResponse getLikeList(HttpServletRequest request, Pageable pageable, LikeDto.Request requestDto) {

        Page<Like> likes = likeRepository.findAllByPost(pageable, requestDto);

        // 팔로우 처리
        Long userId = jwtService.getIdDirectHeader(request);
        List<LikeDto.Response> response;
        if (userId != null) {
            // 로그인 O
            List<Long> userIds = likes.stream().map(l -> l.getUser().getId()).collect(Collectors.toList());
            List<Follow> follows = followRepository.findAllByToUserIn(userId, userIds);

            response = likes.getContent().stream()
                    .map(l -> {
                        Boolean isFollow = follows.stream().anyMatch(f ->
                                f.getToUser().getId() == l.getUser().getId());
                        return LikeDto.Response.fromEntity(l, isFollow);
                    }).collect(Collectors.toList());
        } else {
            // 로그인 X
            response = likes.getContent().stream()
                    .map(l -> LikeDto.Response.fromEntity(l, false))
                    .collect(Collectors.toList());
        }
        return LikeDto.ListResponse.from(likes, response);
    }

    public LikeDto.UpdateResponse like(LikeDto.Request requestDto) {

        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));


    }
}
