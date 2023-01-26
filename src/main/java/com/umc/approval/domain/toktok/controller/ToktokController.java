package com.umc.approval.domain.toktok.controller;

import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.toktok.dto.ToktokDto;
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

    @GetMapping("/search")
    public ResponseEntity<DocumentDto.SearchResponse> search(
            @RequestParam("query") String query,
            @RequestParam("isTag") Integer isTag,
            @RequestParam(value = "category", required = false) Integer category,
            @RequestParam("sortBy") Integer sortBy
    ) {
        return ResponseEntity.ok(toktokService.search(query, isTag, category, sortBy));
    }
}
