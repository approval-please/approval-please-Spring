package com.umc.approval.domain.like_category.service;

import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.like_category.dto.LikeCategoryDto;
import com.umc.approval.domain.like_category.entity.LikeCategory;
import com.umc.approval.domain.like_category.entity.LikeCategoryRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomErrorType;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.CategoryType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class LikeCategoryService {

    private final LikeCategoryRepository likeCategoryRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LikeCategoryDto.Response getLikeCategoryList(HttpServletRequest request) {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(CustomErrorType.USER_NOT_FOUND));

        List<Integer> currentLikeCategoryList = likeCategoryRepository.findByUserId(user.getId())
                .stream().map(l -> l.getCategory().getValue()).collect(Collectors.toList());

        if (currentLikeCategoryList.isEmpty()) {
            return new LikeCategoryDto.Response(false,null);
        }

        return new LikeCategoryDto.Response(true, currentLikeCategoryList);
    }

    public void likeCategory(LikeCategoryDto.Request likeCategoryRequest) {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(CustomErrorType.USER_NOT_FOUND));

        // 기존 DB에 저장되어있던 카테고리 리스트 받아오기
        List<LikeCategory> currentLikeCategoryList = likeCategoryRepository.findByUserId(user.getId());

        if(currentLikeCategoryList.isEmpty())
        {
            createLikeCategory(likeCategoryRequest.getLikedCategory(), user);
        }
        else {
            List<Integer> current = currentLikeCategoryList.stream().map(l -> l.getCategory().getValue()).collect(Collectors.toList());
            List<Integer> post = likeCategoryRequest.getLikedCategory();

            List<Integer> delete = current.stream().filter(c -> !post.contains(c)).collect(Collectors.toList());
            List<Integer> add = post.stream().filter(p -> !current.contains(p)).collect(Collectors.toList());

            deleteLikeCategory(delete, user);
            createLikeCategory(add, user);
        }
    }

   private void createLikeCategory (List<Integer> likeCategories, User user) {
        if(likeCategories != null) {
            for (Integer likeCategory : likeCategories) {
                LikeCategory newLikeCategory = LikeCategory.builder()
                        .user(user)
                        .category(findCategory(likeCategory))
                        .seq(0)
                        .build();
                likeCategoryRepository.save(newLikeCategory);
            }
        }
   }

    private void deleteLikeCategory (List<Integer> likeCategories, User user) {
        if(likeCategories != null) {
            for (Integer likeCategory : likeCategories) {
                likeCategoryRepository.deleteByCategoryAndUserId(findCategory(likeCategory), user.getId());
            }
        }
    }

    private CategoryType findCategory(Integer category) {
        return Arrays.stream(CategoryType.values())
                .filter(c -> c.getValue() == category)
                .findAny().get();
    }

}
