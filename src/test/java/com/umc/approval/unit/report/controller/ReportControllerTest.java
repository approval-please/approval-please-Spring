package com.umc.approval.unit.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.report.controller.ReportController;
import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.report.service.ReportService;
import com.umc.approval.domain.scrap.entity.ScrapRepository;
import com.umc.approval.domain.tag.entity.TagRepository;
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
@WebMvcTest(controllers = ReportController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        })
public class ReportControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ReportService reportService;

    @MockBean
    JwtService jwtService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    LinkRepository linkRepository;

    @MockBean
    TagRepository tagRepository;

    @MockBean
    ReportRepository reportRepository;

    @MockBean
    DocumentRepository documentRepository;

    @MockBean
    ImageRepository imageRepository;

    @MockBean
    LikeRepository likeRepository;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    ScrapRepository scrapRepository;

    @MockBean
    FollowRepository followRepository;

    @MockBean
    ApprovalRepository approvalRepository;

    @DisplayName("결재보고서 검색에 성공한다")
    @WithMockUser
    @Test
    void search_report_success() throws Exception {

        // given
        ReportDto.SearchListResponse content = ReportDto.SearchListResponse.builder()
                .reportId(23L)
                .build();

        ReportDto.SearchResponse response = ReportDto.SearchResponse.builder()
                .reportCount(1)
                .content(List.of(content))
                .build();
        given(reportService.search(any(), any(), any(), any())).willReturn(response);

        // when & then
        mvc.perform(get("/community/reports/search")
                        .queryParam("query", "test")
                        .queryParam("isTag", "1")
                        .param("sortBy", "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportCount").value(response.getReportCount()))
                .andExpect(jsonPath("$.content[0].reportId").value(content.getReportId()))
                .andDo(print());
    }

    @DisplayName("결재보고서 검색 시 필수 파라미터가 없으면 실패한다")
    @WithMockUser
    @Test
    void search_report_no_required_param_fail() throws Exception {

        // given & when & then
        mvc.perform(get("/community/reports/search")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}
