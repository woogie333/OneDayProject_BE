package com.knuaf.oneday.controller;

import com.knuaf.oneday.dto.GraduationCheckResponse;
import com.knuaf.oneday.service.IntegratedGraduationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/graduation")
@RequiredArgsConstructor
public class GraduationController {

    // 통합 서비스만 주입받으면 됨
    private final IntegratedGraduationService integratedService;

    // GET /api/graduation/{studentId}
    @GetMapping("/{studentId}")
    public ResponseEntity<GraduationCheckResponse> checkGraduation(@PathVariable Long studentId) {
        GraduationCheckResponse response = integratedService.checkGraduation(studentId);
        return ResponseEntity.ok(response);
    }
}