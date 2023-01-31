package com.umc.approval.domain.toktok.controller;

import com.umc.approval.domain.report.dto.ReportDto;
import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.toktok.dto.ToktokDto.GetToktokResponse;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.toktok.service.ToktokService;
import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<Void> createPost(
        @Valid @RequestBody ToktokDto.PostToktokRequest request) {
        toktokService.createPost(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<ToktokDto.SearchResponse> getDocumentList(@RequestParam(value = "sortBy", required = false) Integer sortBy) {
        return ResponseEntity.ok(toktokService.getDocumentList(sortBy));
    }

    @GetMapping("/{toktokId}")
    public ResponseEntity<ToktokDto.GetToktokResponse> getToktok(HttpServletRequest request,
        @PathVariable("toktokId") Long id) {
        return ResponseEntity.ok().body(toktokService.getToktok(id, request));
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
    public ResponseEntity<ToktokDto.SearchResponse> search(
        @RequestParam("query") String query,
        @RequestParam("isTag") Integer isTag,
        @RequestParam(value = "category", required = false) Integer category,
        @RequestParam("sortBy") Integer sortBy
    ) {
        return ResponseEntity.ok(toktokService.search(query, isTag, category, sortBy));
    }

    //투표하기
    @PostMapping("/votes/{voteId}")
    public ResponseEntity<ToktokDto.VotePeopleEachOptionResponse> vote(
        @Valid @RequestBody ToktokDto.VoteRequest request, @PathVariable("voteId") Long id) {
        return ResponseEntity.ok(toktokService.vote(request, id));
    }

    @PostMapping("/endVote/{voteId}")
    public ResponseEntity<Void> endVote(@PathVariable("voteId") Long id) {
        toktokService.endVote(id);
        return ResponseEntity.ok().build();
    }

    //투표한 사람 목록
    @GetMapping("/votes/{voteOptionId}")
    public ResponseEntity<ToktokDto.GetVotePeopleListResponse> getVotePeopleList(@PathVariable("voteOptionId") Long voteOptionId) {
        return ResponseEntity.ok(toktokService.getVotePeopleList(voteOptionId));
    }
}