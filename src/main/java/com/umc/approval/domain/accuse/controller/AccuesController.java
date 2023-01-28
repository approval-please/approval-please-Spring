package com.umc.approval.domain.accuse.controller;

import com.umc.approval.domain.accuse.dto.AccuseDto;
import com.umc.approval.domain.accuse.service.AccuseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/accuse")
@RequiredArgsConstructor
@RestController
public class AccuesController {

    private final AccuseService accuseService;

    @PostMapping
    public ResponseEntity<Void> accuse(
            @RequestBody AccuseDto.Request accuseRequest
            ) {
        accuseService.accuse(accuseRequest);
        return ResponseEntity.ok().build();
    }
}
