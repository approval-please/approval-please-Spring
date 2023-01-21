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
import com.umc.approval.global.aws.service.AwsS3Service;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    AwsS3Service awsS3Service;

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

    private MockMultipartFile createImage() throws IOException {
        String fileName = "test";
        String contentType = "image/png";
        String filePath = "src/test/resources/img/test.png";
        return new MockMultipartFile("images", fileName, contentType, new FileInputStream(filePath));
    }

    @DisplayName("댓글 등록에 성공한다 - 이미지 X")
    @WithMockUser
    @Test
    void create_comment_success() throws Exception {

        // given
        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(1L)
                .content("test")
                .build();
        String body = mapper.writeValueAsString(requestDto);
        MockMultipartFile bodyFile = new MockMultipartFile("data", "data",
                "application/json", body.getBytes(StandardCharsets.UTF_8));

        // when & then
        mvc.perform(multipart("/comments")
                        .file(bodyFile)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("댓글 등록에 성공한다 - 이미지 O")
    @WithMockUser
    @Test
    void create_comment_with_image_success() throws Exception {

        // given
        CommentDto.CreateRequest requestDto = CommentDto.CreateRequest.builder()
                .documentId(1L)
                .content("test")
                .build();
        String body = mapper.writeValueAsString(requestDto);
        MockMultipartFile bodyFile = new MockMultipartFile("data", "data",
                "application/json", body.getBytes(StandardCharsets.UTF_8));

        // when & then
        mvc.perform(multipart("/comments")
                        .file(bodyFile)
                        .file(createImage())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("댓글 등록 시 body가 없으면 실패한다")
    @WithMockUser
    @Test
    void create_comment_no_body_fail() throws Exception {
        // given & when & then
        mvc.perform(multipart("/comments")
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
        MockMultipartFile bodyFile = new MockMultipartFile("data", "data",
                "application/json", body.getBytes(StandardCharsets.UTF_8));

        // when & then
        mvc.perform(multipart("/comments")
                        .file(bodyFile)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("댓글 수정에 성공한다 - 이미지 X")
    @WithMockUser
    @Test
    void update_comment_success() throws Exception {

        // given
        CommentDto.UpdateRequest requestDto = new CommentDto.UpdateRequest("updateContent");
        String body = mapper.writeValueAsString(requestDto);
        MockMultipartFile bodyFile = new MockMultipartFile("data", "data",
                "application/json", body.getBytes(StandardCharsets.UTF_8));

        // when & then
        mvc.perform(multipart(PUT, "/comments/1")
                        .file(bodyFile)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("댓글 수정에 성공한다 - 이미지 O")
    @WithMockUser
    @Test
    void update_comment_with_image_success() throws Exception {

        // given
        CommentDto.UpdateRequest requestDto = new CommentDto.UpdateRequest("updateContent");
        String body = mapper.writeValueAsString(requestDto);
        MockMultipartFile bodyFile = new MockMultipartFile("data", "data",
                "application/json", body.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile image = createImage();

        // when & then
        mvc.perform(multipart(PUT, "/comments/1")
                        .file(bodyFile)
                        .file(image)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("댓글 수정 시 body가 없으면 실패한다")
    @WithMockUser
    @Test
    void update_comment_no_body_fail() throws Exception {
        // given & when & then
        mvc.perform(multipart(PUT, "/comments/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @DisplayName("댓글 수정 시 댓글 내용이 없으면 실패한다")
    @WithMockUser
    @Test
    void update_comment_no_content_fail() throws Exception {

        // given
        CommentDto.UpdateRequest requestDto = new CommentDto.UpdateRequest(null);
        String body = mapper.writeValueAsString(requestDto);
        MockMultipartFile bodyFile = new MockMultipartFile("data", "data",
                "application/json", body.getBytes(StandardCharsets.UTF_8));

        // when & then
        mvc.perform(multipart(PUT, "/comments/1")
                        .file(bodyFile)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
