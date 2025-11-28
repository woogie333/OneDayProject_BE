package com.knuaf.oneday.controller;

import com.knuaf.oneday.dto.GraduationCheckResponse;
import com.knuaf.oneday.service.GSWGraduationCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/graduation")
@RequiredArgsConstructor
public class GSWCheckController {

    private final GSWGraduationCheckService globalService;

    // GET /api/globalsw/check/{studentId}
    // 파라미터 없이 깔끔하게 호출 가능
    @GetMapping("/globalsw/{studentId}")
    public ResponseEntity<GraduationCheckResponse> check(@PathVariable Long studentId) {
        return ResponseEntity.ok(globalService.checkGraduation(studentId));
    }
}