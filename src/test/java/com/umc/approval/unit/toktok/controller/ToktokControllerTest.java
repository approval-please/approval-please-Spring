package com.umc.approval.unit.toktok.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.scrap.entity.ScrapRepository;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.toktok.controller.ToktokController;
import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.toktok.service.ToktokService;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.domain.vote.entity.UserVoteRepository;
import com.umc.approval.domain.vote.entity.VoteOptionRepository;
import com.umc.approval.domain.vote.entity.VoteRepository;
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

import javax.persistence.EntityManager;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = ToktokController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        })
public class ToktokControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ToktokService toktokService;

    @MockBean
    JwtService jwtService;

    @MockBean
    ToktokRepository toktokRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    VoteRepository voteRepository;

    @MockBean
    VoteOptionRepository voteOptionRepository;

    @MockBean
    LinkRepository linkRepository;

    @MockBean
    TagRepository tagRepository;

    @MockBean
    ImageRepository imageRepository;

    @MockBean
    EntityManager entityManager;

    @MockBean
    UserVoteRepository userVoteRepository;

    @MockBean
    LikeRepository likeRepository;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    ScrapRepository scrapRepository;


    @DisplayName("결재톡톡 검색에 성공한다")
    @WithMockUser
    @Test
    void search_toktok_success() throws Exception {

        // given
        ToktokDto.SearchListResponse content = ToktokDto.SearchListResponse.builder()
                .toktokId(1L)
                .category(0)
                .build();
        ToktokDto.SearchResponse response = ToktokDto.SearchResponse.builder()
                .toktokCount(1)
                .content(List.of(content))
                .build();
        given(toktokService.search(any(), any(), any(), any())).willReturn(response);

        // when & then
        mvc.perform(get("/community/toktoks/search")
                        .queryParam("query", "test")
                        .queryParam("isTag", "1")
                        .param("sortBy", "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toktokCount").value(response.getToktokCount()))
                .andExpect(jsonPath("$.content[0].category").value(content.getCategory()))
                .andDo(print());
    }

    @DisplayName("결재톡톡 검색 시 필수 파라미터가 없으면 실패한다")
    @WithMockUser
    @Test
    void search_toktok_no_required_param_fail() throws Exception {

        // given & when & then
        mvc.perform(get("/community/toktoks/search")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}
