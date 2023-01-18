package com.umc.approval.domain.toktok.controller;

import com.umc.approval.domain.toktok.dto.ToktokRequestDto;
import com.umc.approval.domain.toktok.service.ToktokService;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RestController
public class ToktokApiController {

    @Autowired
    private final ToktokService toktokService;

    @PostMapping("/community/toktoks")
    public ResponseEntity<Void> createPost(@Valid
    @RequestPart(value = "data", required = false) ToktokRequestDto toktokRequestDto,
        @RequestPart(value = "images", required = false) List<MultipartFile> files) {

        toktokService.createPost(toktokRequestDto, files);
        return ResponseEntity.ok().build();
    }

}
