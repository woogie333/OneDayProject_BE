package com.knuaf.oneday.controller;

import com.knuaf.oneday.dto.LectureResponseDto;
import com.knuaf.oneday.service.CourseService; // 또는 LectureService
import com.knuaf.oneday.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecture") // URL 경로 구분
@RequiredArgsConstructor
public class LectureController {

    private final CourseService courseService;
    private final LectureService lectureService;

    // GET /api/lecture/list?semester=2024-2&keyword=컴퓨터
    @GetMapping("/list")
    public ResponseEntity<List<LectureResponseDto>> getLectureList(
            @RequestParam String semester, // 필수 파라미터
            @RequestParam(required = false) String keyword // 선택 파라미터 (없을 수 있음)
    ) {
        List<LectureResponseDto> lectures = lectureService.getLectureList(semester, keyword);
        return ResponseEntity.ok(lectures);
    }
}