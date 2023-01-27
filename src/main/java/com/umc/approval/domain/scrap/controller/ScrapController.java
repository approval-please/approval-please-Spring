package com.umc.approval.domain.scrap.controller;

import com.umc.approval.domain.scrap.dto.ScrapDto;
import com.umc.approval.domain.scrap.service.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/scrap")
@RequiredArgsConstructor
@RestController
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping
    public ResponseEntity<ScrapDto.UpdateResponse> scrap(
            @RequestBody ScrapDto.Request scrapRequest
            ) {
        return ResponseEntity.ok(scrapService.scrap(scrapRequest));
    }
}
