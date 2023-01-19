package com.umc.approval.unit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.user.controller.UserController;
import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.domain.user.service.UserService;
import com.umc.approval.global.aws.service.AwsS3Service;
import com.umc.approval.global.security.SecurityConfig;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.SocialType;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        })
public class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AwsS3Service awsS3Service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @DisplayName("sns 회원가입에 성공한다 - 카카오")
    @WithMockUser
    @Test
    void sns_signup_kakao_success() throws Exception {

        // given
        UserDto.SnsRequest requestDto = new UserDto.SnsRequest(
                "test", "test@test.com", "010-1234-5678", SocialType.KAKAO, 12345L);
        String body = new ObjectMapper().writeValueAsString(requestDto);

        // when & then
        mvc.perform(post("/auth/signup/sns")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("sns 회원가입 시 body가 없으면 실패한다")
    @WithMockUser
    @Test
    void sns_signup_no_body_fail() throws Exception {

        // given & when & then
        mvc.perform(post("/auth/signup/sns")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @DisplayName("logout에 성공한다")
    @WithMockUser
    @Test
    void logout_success() throws Exception {
        // given & when & then
        mvc.perform(post("/auth/logout")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @DisplayName("refresh token을 통해 access(refresh) token 재발급에 성공한다")
    @WithMockUser
    @Test
    void refresh_success() throws Exception {
        // given
        String token = "Bearer test123";
        given(userService.refresh(any())).willReturn(new UserDto.NormalTokenResponse("test1", "test2"));

        // when & then
        mvc.perform(post("/auth/refresh")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("비밀번호 재설정에 성공한다")
    @WithMockUser
    @Test
    void reset_password_success() throws Exception {
        // given
        UserDto.ResetPasswordRequest requestDto = new UserDto.ResetPasswordRequest("test@test.com", "test1234!");
        String body = new ObjectMapper().writeValueAsString(requestDto);

        // when & then
        mvc.perform(post("/auth/reset")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("비밀번호 재설정 시, body가 없으면 실패한다")
    @WithMockUser
    @Test
    void reset_password_no_body_fail() throws Exception {
        // given & when & then
        mvc.perform(post("/auth/reset")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}
