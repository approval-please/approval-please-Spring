package com.umc.approval.unit.document.service;

import com.umc.approval.domain.approval.entity.ApprovalRepository;
import com.umc.approval.domain.comment.entity.CommentRepository;
import com.umc.approval.domain.document.entity.DocumentRepository;
import com.umc.approval.domain.document.service.DocumentService;
import com.umc.approval.domain.image.entity.ImageRepository;
import com.umc.approval.domain.like.entity.LikeRepository;
import com.umc.approval.domain.link.entity.LinkRepository;
import com.umc.approval.domain.tag.entity.TagRepository;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.security.service.JwtService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @InjectMocks
    DocumentService documentService;

    @Mock
    JwtService jwtService;

    @Mock
    DocumentRepository documentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    TagRepository tagRepository;

    @Mock
    ImageRepository imageRepository;

    @Mock
    LikeRepository likeRepository;

    @Mock
    LinkRepository linkRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ApprovalRepository approvalRepository;

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .email("test" + id + "@test.com")
                .password("test123!" + id)
                .nickname("test" + id)
                .build();
    }
}
