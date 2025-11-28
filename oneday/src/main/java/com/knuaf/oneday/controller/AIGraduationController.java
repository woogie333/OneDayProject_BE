package com.knuaf.oneday.controller;

import com.knuaf.oneday.dto.GraduationCheckResponse;
import com.knuaf.oneday.service.AIGraduationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/graduation")
@RequiredArgsConstructor
public class AIGraduationController {

    private final AIGraduationService aiService;

    @GetMapping("/ai/{studentId}")
    public ResponseEntity<GraduationCheckResponse> check(@PathVariable Long studentId) {
        return ResponseEntity.ok(aiService.checkGraduation(studentId));
    }
}