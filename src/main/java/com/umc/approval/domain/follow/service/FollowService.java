package com.umc.approval.domain.follow.service;

import com.umc.approval.domain.follow.dto.FollowDto;
import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Transactional
@RequiredArgsConstructor
@Service
public class FollowService {

    private final JwtService jwtService;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowDto.UpdateResponse follow(FollowDto.Request followRequest) {
        // 본인이 본인을 팔로우하는 경우 예외처리
        if(jwtService.getId().equals(followRequest.getToUserId())){
            throw new CustomException(SELF_FOLLOW_NOT_ALLOWED);
        }

        User fromUser = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Optional<Follow> findFollow = followRepository.findByFromUserIdAndToUserId(fromUser.getId(), followRequest.getToUserId());

        // 팔로잉인 경우 -> 취소
        if(findFollow.isPresent()) {
            followRepository.delete(findFollow.get());
            return new FollowDto.UpdateResponse(false);
        }

        // 팔로우
        User toUser = null;

        if(followRequest.getToUserId() != null) {
            toUser = userRepository.findById(followRequest.getToUserId())
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        }

        Follow follow = followRequest.toEntity(fromUser, toUser);
        followRepository.save(follow);

        return new FollowDto.UpdateResponse(true);
    }
}