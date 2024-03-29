package com.umc.approval.unit.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.comment.controller.CommentController;
import com.umc.approval.domain.comment.dto.CommentDto;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.comment.service.CommentService;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = CommentController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        })
public class CommentControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CommentService commentService;

    @MockBean
    JwtService jwtService;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    DocumentRepository documentRepository;

    @MockBean
    ReportRepository reportRepository;

    @MockBean
    ToktokRepository toktokRepository;

    @DisplayName("댓글 등록에 성공한다")
    @WithMockUser
    @Test
    void create_comment_success() throws Exception {

        // given
        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(1L)
                .content("test")
                .build();
        String body = mapper.writeValueAsString(requestDto);

        // when & then
        mvc.perform(post("/comments")
                        .content(body)
                        .contentType(APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("댓글 등록 시 body가 없으면 실패한다")
    @WithMockUser
    @Test
    void create_comment_no_body_fail() throws Exception {
        // given & when & then
        mvc.perform(post("/comments")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @DisplayName("댓글 등록 시 댓글 내용이 없으면 실패한다")
    @WithMockUser
    @Test
    void create_comment_no_content_fail() throws Exception {

        // given
        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(1L)
                .build();
        String body = mapper.writeValueAsString(requestDto);

        // when & then
        mvc.perform(post("/comments")
                        .content(body)
                        .contentType(APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("댓글 수정에 성공한다")
    @WithMockUser
    @Test
    void update_comment_success() throws Exception {

        // given
        CommentDto.UpdateRequest requestDto = new CommentDto.UpdateRequest("updateContent", null);
        String body = mapper.writeValueAsString(requestDto);

        // when & then
        mvc.perform(put("/comments/1")
                        .content(body)
                        .contentType(APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("댓글 수정 시 body가 없으면 실패한다")
    @WithMockUser
    @Test
    void update_comment_no_body_fail() throws Exception {
        // given & when & then
        mvc.perform(put("/comments/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @DisplayName("댓글 수정 시 댓글 내용이 없으면 실패한다")
    @WithMockUser
    @Test
    void update_comment_no_content_fail() throws Exception {

        // given
        CommentDto.UpdateRequest requestDto = new CommentDto.UpdateRequest(null, null);
        String body = mapper.writeValueAsString(requestDto);

        // when & then
        mvc.perform(put("/comments/1")
                        .content(body)
                        .contentType(APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("댓글 삭제에 성공한다")
    @WithMockUser
    @Test
    void delete_comment_success() throws Exception {
        // given & when & then
        mvc.perform(delete("/comments/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
