package com.umc.approval.domain.toktok.controller;

import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.toktok.service.ToktokService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/community/toktoks")
@RestController
@Slf4j
public class ToktokController {

    private final ToktokService toktokService;

    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody ToktokDto.PostToktokRequest request) {
        toktokService.createPost(request);
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
