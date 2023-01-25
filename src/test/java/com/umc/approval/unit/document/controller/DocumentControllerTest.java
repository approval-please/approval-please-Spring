package com.umc.approval.unit.document.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.controller.DocumentController;
import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.document.service.DocumentService;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.link.entity.LinkRepository;
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
@WebMvcTest(controllers = DocumentController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        })
public class DocumentControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    DocumentService documentService;

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
    LikeRepository likeRepository;

    @MockBean
    LinkRepository linkRepository;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    ApprovalRepository approvalRepository;

    @DisplayName("결재서류 검색에 성공한다")
    @WithMockUser
    @Test
    void search_document_success() throws Exception {

        // given
        DocumentDto.SearchListResponse content = DocumentDto.SearchListResponse.builder()
                .documentId(1L)
                .category(0)
                .state(2)
                .title("test")
                .content("test")
                .imageCount(0)
                .view(1L)
                .approvalCount(3)
                .rejectCount(3)
                .build();
        DocumentDto.SearchResponse response = DocumentDto.SearchResponse.builder()
                .page(0)
                .totalPage(2)
                .totalElement(55L)
                .content(List.of(content))
                .build();
        given(documentService.search(any(), any(), any(), any(), any(), any())).willReturn(response);

        // when & then
        mvc.perform(get("/documents/search")
                        .queryParam("query", "test")
                        .queryParam("isTag", "1")
                        .param("sortBy", "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(response.getPage()))
                .andExpect(jsonPath("$.content[0].title").value(response.getContent().get(0).getTitle()))
                .andDo(print());
    }

    @DisplayName("결재서류 검색 시 필수 파라미터가 없으면 실패한다")
    @WithMockUser
    @Test
    void search_document_no_required_param_fail() throws Exception {

        // given & when & then
        mvc.perform(get("/documents/search")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}
