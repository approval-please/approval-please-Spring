package com.umc.approval.unit.like.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.like.controller.LikeController;
import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.like.service.LikeService;
import com.umc.approval.global.security.SecurityConfig;
import com.umc.approval.global.security.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = LikeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        })
public class LikeControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    LikeService likeService;

    @MockBean
    JwtService jwtService;

    @MockBean
    LikeRepository likeRepository;

    @MockBean
    FollowRepository followRepository;

    @DisplayName("좋아요 목록 조회에 성공한다")
    @WithMockUser
    @Test
    void get_like_list_success() throws Exception {

        // given
        LikeDto.ListRequest requestDto = LikeDto.ListRequest.builder()
                .documentId(1L)
                .build();

        String body = mapper.writeValueAsString(requestDto);

        // when & then
        mvc.perform(get("/likes")
                        .content(body)
                        .contentType(APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("좋아요 목록 조회 시, body가 없으면 실패한다")
    @WithMockUser
    @Test
    void get_like_list_no_body_fail() throws Exception {

        // given & when & then
        mvc.perform(get("/likes")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}