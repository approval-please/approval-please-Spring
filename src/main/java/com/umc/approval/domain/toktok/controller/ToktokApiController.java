package com.umc.approval.domain.toktok.controller;

import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.toktok.entity.ToktokRepository;
import com.umc.approval.domain.toktok.service.ToktokService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RestController
@Slf4j
public class ToktokApiController {

    @Autowired
    private final ToktokService toktokService;

    @Autowired
    private final ToktokRepository toktokRepository;

    @PostMapping("/community/toktoks")
    public ResponseEntity<Void> createPost(@Valid
        @RequestPart(value = "data", required = false) ToktokDto.PostToktokRequest request,
        @RequestPart(value = "images", required = false) List<MultipartFile> files) {

        toktokService.createPost(request, files);
        return ResponseEntity.ok().build();
    }
}
