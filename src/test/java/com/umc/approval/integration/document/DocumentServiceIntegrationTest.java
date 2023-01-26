package com.umc.approval.integration.document;

import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.document.service.DocumentService;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
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
public class DocumentServiceIntegrationTest {

    @Autowired
    DocumentService documentService;

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
    LikeRepository likeRepository;

    @Autowired
    LinkRepository linkRepository;

    @Autowired
    CommentRepository commentRepository;

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

    private Document createDocument(User user) {
        Document document = Document.builder()
                .user(user)
                .title("title")
                .content("content")
                .view(0L)
                .category(ANIMAL_PLANT)
                .state(2)
                .notification(true)
                .build();
        return documentRepository.save(document);
    }

    private void createTag(String tag, Document document) {
        Tag build = Tag.builder()
                .document(document)
                .tag(tag)
                .build();
        tagRepository.save(build);
    }

    @DisplayName("결재서류 검색에 성공한다 - 카테고리 / 상태 검색")
    @Test
    void search_document_success() {

        // given
        User user = createUser(1L);
        userRepository.save(user);

        Document document1 = Document.builder()
                .user(user)
                .title("아이폰 테스트")
                .content("테스트")
                .view(0L)
                .category(ANIMAL_PLANT)
                .state(2)
                .notification(true)
                .build();

        Document document2 = Document.builder()
                .user(user)
                .title("에어폰 테스트")
                .content("테아이폰테스트")
                .view(0L)
                .category(ANIMAL_PLANT)
                .state(1)
                .notification(true)
                .build();

        Document document3 = Document.builder()
                .user(user)
                .title("에어폰 테스트")
                .content("테에어폰테스트")
                .view(0L)
                .category(DIGITAL)
                .state(2)
                .notification(true)
                .build();

        documentRepository.save(document1);
        documentRepository.save(document2);
        documentRepository.save(document3);

        // when
        DocumentDto.SearchResponse response =
                documentService.search("아이", 0, ANIMAL_PLANT.getValue(), 1, 0);
        DocumentDto.SearchListResponse findResponse = response.getContent().get(0);

        // then
        assertThat(response.getContent().size()).isEqualTo(1);
        assertThat(findResponse.getTitle()).isEqualTo(document2.getTitle());
    }
}
