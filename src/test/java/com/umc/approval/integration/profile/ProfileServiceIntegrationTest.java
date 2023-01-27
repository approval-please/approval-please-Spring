package com.umc.approval.integration.profile;

import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.performance.entity.PerformanceRepository;
import com.umc.approval.domain.profile.dto.ProfileDto;
import com.umc.approval.domain.profile.service.ProfileService;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.security.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class ProfileServiceIntegrationTest {

    @Autowired
    ProfileService profileService;

    @Autowired
    EntityManager em;

    @Autowired
    JwtService jwtService;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ApprovalRepository approvalRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    PerformanceRepository performanceRepository;

    private User createUser(Long id) {
        return User.builder()
                .email("test" + id + "@test.com")
                .password("test123!")
                .nickname("test" + id)
                .phoneNumber("010-1234-5678")
                .level(0)
                .promotionPoint(0L)
                .build();
    }

    private User createUser(Long id, String nickname) {
        return User.builder()
                .email("test" + id + "@test.com")
                .password("test123!")
                .nickname(nickname)
                .phoneNumber("010-1234-5678")
                .level(0)
                .promotionPoint(0L)
                .build();
    }

    @DisplayName("사원 검색에 성공한다")
    @Test
    void search_user_success() {

        // given
        User user1 = createUser(1L, "apple");
        User user2 = createUser(2L, "aapppllo");
        User user3 = createUser(3L, "hello");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // when
        ProfileDto.SearchResponse response = profileService.search("app");
        List<ProfileDto.SearchListResponse> content = response.getContent();

        // then
        assertThat(response.getUserCount()).isEqualTo(2);
        assertThat(content.size()).isEqualTo(2);
        assertThat(content.get(0).getNickname()).isEqualTo(user1.getNickname());
        assertThat(content.get(1).getNickname()).isEqualTo(user2.getNickname());
    }
}
