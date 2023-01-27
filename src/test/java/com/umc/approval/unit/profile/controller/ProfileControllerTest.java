package com.umc.approval.unit.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.performance.entity.PerformanceRepository;
import com.umc.approval.domain.profile.controller.ProfileController;
import com.umc.approval.domain.profile.dto.ProfileDto;
import com.umc.approval.domain.profile.service.ProfileService;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = ProfileController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        })
public class ProfileControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ProfileService profileService;

    @MockBean
    JwtService jwtService;

    @MockBean
    DocumentRepository documentRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    TagRepository tagRepository;

    @MockBean
    ImageRepository imageRepository;

    @MockBean
    ApprovalRepository approvalRepository;

    @MockBean
    FollowRepository followRepository;

    @MockBean
    PerformanceRepository performanceRepository;

    @DisplayName("사원 검색에 성공한다")
    @WithMockUser
    @Test
    void search_user_success() throws Exception {

        // given
        User user = User.builder()
                .id(1L)
                .nickname("hunseong")
                .level(3)
                .build();
        ProfileDto.SearchResponse response = ProfileDto.SearchResponse.from(List.of(user));
        given(profileService.search(any())).willReturn(response);

        // when & then
        mvc.perform(get("/profile/search")
                        .param("query", "hun")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userCount").value(1))
                .andExpect(jsonPath("$.content[0].nickname").value(user.getNickname()))
                .andDo(print());
    }

    @DisplayName("사원 검색 시 필수 파라미터가 없으면 실패한다")
    @WithMockUser
    @Test
    void search_user_no_required_param_fail() throws Exception {

        // given & when & then
        mvc.perform(get("/profile/search")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}
