package com.knuaf.roadmap.service;

import com.knuaf.roadmap.domain.Course;
import com.knuaf.roadmap.dto.CourseRequest;
import com.knuaf.roadmap.dto.CourseResponse;
import com.knuaf.roadmap.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 조회 모드 (성능 최적화)
public class CourseService {

    private final CourseRepository courseRepository;

    // 목록 조회 (GET)
    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream()
                // ⚠️ 수정된 부분: CourseResponse::new -> CourseResponse::from
                // 우리가 DTO에 만들어둔 'from' 메서드를 사용하여 변환합니다.
                .map(CourseResponse::from)
                .collect(Collectors.toList());
    }
}