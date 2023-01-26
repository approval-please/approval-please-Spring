package com.umc.approval.domain.toktok.controller;

import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.toktok.dto.ToktokDto.GetToktokResponse;
import com.umc.approval.domain.toktok.service.ToktokService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


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

    @GetMapping("/{toktokId}")
    public ResponseEntity<ToktokDto.GetToktokResponse> getReport(@PathVariable("toktokId") Long id) {
        return ResponseEntity.ok().body(toktokService.getToktok(id));
    }


    @PutMapping("/{toktokId}")
    public ResponseEntity<Void> updatePost(
            @Valid @RequestBody ToktokDto.PostToktokRequest request,
            @PathVariable("toktokId") Long id
    ) {
        toktokService.updatePost(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{toktokId}")
    public ResponseEntity<Void> deletePost(@PathVariable("toktokId") Long id) {
        toktokService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}
