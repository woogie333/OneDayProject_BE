// src/main/java/com/example/roadmap/controller/CourseController.java

package com.knuaf.roadmap.controller;

import com.knuaf.roadmap.dto.CourseRequest;
import com.knuaf.roadmap.dto.CourseResponse;
import com.knuaf.roadmap.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses") // 공통 URI 접두사
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8080") // 3000번 포트 허용
public class CourseController {

    private final CourseService courseService;

    // 학점 목록 조회 (GET)
    // URI: GET /api/v1/courses
    @GetMapping
    public ResponseEntity<List<CourseResponse>> findAllCourses() {
        List<CourseResponse> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }


}