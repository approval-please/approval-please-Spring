package com.umc.approval.integration.report;

import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.follow.entity.FollowRepository;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.report.entity.ReportRepository;
import com.umc.approval.domain.report.service.ReportService;
import com.umc.approval.domain.scrap.entity.ScrapRepository;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static com.umc.approval.global.type.CategoryType.ANIMAL_PLANT;
import static com.umc.approval.global.type.CategoryType.DIGITAL;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class ReportServiceIntegrationTest {

    @Autowired
    ReportService reportService;

    @Autowired
    EntityManager em;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LinkRepository linkRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ScrapRepository scrapRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    ApprovalRepository approvalRepository;

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

    private void loginUser(User user) {
        String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        Authentication authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @DisplayName("결재보고서 검색에 성공한다 - 카테고리 검색")
    @Test
    void search_report_success() {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        Document document1 = Document.builder()
                .user(user)
                .category(DIGITAL)
                .title("test")
                .content("content")
                .notification(true)
                .view(10L)
                .state(2)
                .build();
        documentRepository.save(document1);

        Report report1 = Report.builder()
                .document(document1)
                .content("안녕하세요")
                .view(0L)
                .notification(true)
                .build();
        reportRepository.save(report1);

        Document document2 = Document.builder()
                .user(user)
                .category(DIGITAL)
                .title("test")
                .content("content")
                .notification(true)
                .view(10L)
                .state(2)
                .build();
        documentRepository.save(document2);

        Report report2 = Report.builder()
                .document(document2)
                .content("안녕하2세요")
                .view(0L)
                .notification(true)
                .build();
        reportRepository.save(report2);

        Document document3 = Document.builder()
                .user(user)
                .category(ANIMAL_PLANT)
                .title("test")
                .content("content")
                .notification(true)
                .view(10L)
                .state(2)
                .build();
        documentRepository.save(document3);

        Report report3 = Report.builder()
                .document(document3)
                .content("안녕하세요")
                .view(0L)
                .notification(true)
                .build();
        reportRepository.save(report3);

        // when
        ReportDto.SearchResponse response = reportService.search("하세", 0, DIGITAL.getValue(), 0);
        ReportDto.SearchListResponse findResponse = response.getContent().get(0);

        // then
        assertThat(response.getContent().size()).isEqualTo(1);
        assertThat(findResponse.getReportId()).isEqualTo(report1.getId());
        assertThat(findResponse.getContent()).isEqualTo(report1.getContent());
        assertThat(findResponse.getDocument().getDocumentId()).isEqualTo(report1.getDocument().getId());
    }
}
