package com.umc.approval.domain.toktok.controller;

import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.service.ToktokService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RequestMapping("/community/toktoks")
@RestController
@Slf4j
public class ToktokController {

    private final ToktokService toktokService;

    @PostMapping
    public ResponseEntity<Void> createPost(@Valid
        @RequestPart(value = "data", required = false) ToktokDto.PostToktokRequest request,
        @RequestPart(value = "images", required = false) List<MultipartFile> files) {

        toktokService.createPost(request, files);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{toktokId}")
    public ResponseEntity<Void> updatePost(@Valid
        @RequestPart(value = "data", required = false) ToktokDto.PostToktokRequest request,
        @RequestPart(value = "images", required = false) List<MultipartFile> files,
        @PathVariable("toktokId") Long id) {

        toktokService.updatePost(id, request, files);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{toktokId}")
    public ResponseEntity<Void> deletePost(@PathVariable("toktokId") Long id) {
        toktokService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}
