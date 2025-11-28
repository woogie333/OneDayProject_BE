package com.knuaf.oneday.controller;

import com.knuaf.oneday.dto.CourseRegisterDto;
import com.knuaf.oneday.dto.LectureResponseDto;
import com.knuaf.oneday.entity.User;
import com.knuaf.oneday.repository.UserRepository;
import com.knuaf.oneday.service.CourseService; // 또는 LectureService
import com.knuaf.oneday.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecture") // URL 경로 구분
@RequiredArgsConstructor
public class LectureController {
    UserRepository userRepository;
    private String getSpecificMajor(Authentication authentication) {
        // 1. 시큐리티 컨텍스트에서 로그인 아이디(예: "sion") 꺼내기
        String loginId = authentication.getName();

        // 2. DB에서 유저 정보 찾기
        User user = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 3. 유저 엔티티에 저장된 실제 학번(Long) 반환
        return user.getSpecific_major();
    }
    private final CourseService courseService;
    private final LectureService lectureService;

    // GET /api/lecture/list?semester=2024-2&keyword=컴퓨터
    @GetMapping("/list")
    public ResponseEntity<List<LectureResponseDto>> getLectureList(
            @RequestParam int semester, // 필수 파라미터
            @RequestParam(required = false) String keyword // 선택 파라미터 (없을 수 있음)
    ) {
        List<LectureResponseDto> lectures = lectureService.getLectureList(semester, keyword);
        return ResponseEntity.ok(lectures);
    }
    // 학년별/학기별 권장(기초) 시간표 조회 API
    // GET /api/lecture/standard?grade=1&semester=1
    @GetMapping("/standard")
    public ResponseEntity<List<LectureResponseDto>> getStandardCurriculum(
            Authentication authentication, // ★ 인증 객체 주입
            @RequestParam int grade,    // 학년 (1, 2, 3, 4)
            @RequestParam int semester // 학기 (1:1학기 2:여름계절 3:2학기 4:겨울게절)
    ) {
        String s_major = getSpecificMajor(authentication);
        List<LectureResponseDto> standardList = lectureService.getStandardCourses(s_major, grade, semester);
        return ResponseEntity.ok(standardList);
    }
}