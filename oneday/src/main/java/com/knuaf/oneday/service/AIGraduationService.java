package com.knuaf.oneday.service;

import com.knuaf.oneday.dto.GraduationCheckResponse;
import com.knuaf.oneday.dto.GraduationCheckResponse.CheckItem;
import com.knuaf.oneday.entity.User;
import com.knuaf.oneday.entity.UserAttend;
import com.knuaf.oneday.repository.UserAttendRepository;
import com.knuaf.oneday.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIGraduationService {

    private final UserRepository userRepository;
    private final UserAttendRepository userAttendRepository;

    // 인공지능컴퓨팅 필수 과목
    private static final Map<String, String> REQUIRED_COURSES = new LinkedHashMap<>() {{
        put("COMP0453", "컴퓨팅사고와 SW코딩");
        put("COMP0454", "인공지능수학기초");
        put("COME0331", "자료구조");
        put("COMP0319", "알고리즘1");
        put("COMP0324", "인공지능");
        put("ITEC0417", "기계학습개론");
        put("ITEC0401", "종합설계프로젝트1");
    }};

    @Transactional(readOnly = true)
    public GraduationCheckResponse checkGraduation(Long studentId) {

        // 1. 유저 정보 조회
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User 정보가 없습니다."));

        List<CheckItem> items = new ArrayList<>();
        boolean allPassed = true;

        // ----------------------------------------------------
        // [1] 학점 및 졸업요건 검사
        // ----------------------------------------------------

        // 1. 총 이수학점 (140)
        int totalCredit = (user.getTotal_credit() == null) ? 0 : user.getTotal_credit().intValue();
        if (!addCheckItem(items, "총 이수학점", totalCredit, 140)) allPassed = false;

        // ★ [추가] 2. 전공 이수학점 (72)
        // User 엔티티에서 전공학점 가져오기 (Null 방지)
        int majorCredit = (user.getMajor_credit() == null) ? 0 : user.getMajor_credit().intValue();

        // addCheckItem을 호출하면 자동으로 부족한 점수를 계산해줍니다.
        if (!addCheckItem(items, "전공학점", majorCredit, 72)) allPassed = false;


        // (참고: 영어, 현장실습 등 나머지 요건도 필요하면 여기에 추가하세요)
        /*
        int engScore = (user.getEng_score() == null) ? 0 : user.getEng_score().intValue();
        if (!addCheckItem(items, "영어성적(토익)", engScore, 700)) allPassed = false;

        boolean internship = user.isInternship();
        items.add(CheckItem.builder().category("현장실습").current(internship ? 1 : 0).required(1)
                .isPassed(internship).message(internship ? "이수" : "미이수").build());
        if (!internship) allPassed = false;
        */

        // ----------------------------------------------------
        // [2] 필수 과목 체크 로직 (과목번호 기준)
        // ----------------------------------------------------

        // 1. 학생 수강 내역 조회 (Set 변환)
        List<UserAttend> takenLectures = userAttendRepository.findByStudentId(studentId);

        Set<String> takenCodes = takenLectures.stream()
                .map(UserAttend::getLecId)
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toSet());

        // 2. 필수 과목 미이수 내역 찾기
        List<String> missingCourses = new ArrayList<>();

        for (Map.Entry<String, String> entry : REQUIRED_COURSES.entrySet()) {
            String code = entry.getKey();
            String name = entry.getValue();

            if (!takenCodes.contains(code)) {
                missingCourses.add(name + " (" + code + ")");
            }
        }

        // 3. 결과 반영
        boolean passCourses = missingCourses.isEmpty();

        items.add(CheckItem.builder()
                .category("필수과목 이수")
                .current(REQUIRED_COURSES.size() - missingCourses.size())
                .required(REQUIRED_COURSES.size())
                .isPassed(passCourses)
                .message(passCourses ? "모두 이수함" : missingCourses.size() + "과목 미이수")
                .build());

        if (!passCourses) allPassed = false;

        return GraduationCheckResponse.builder()
                .studentId(studentId)
                .isGraduationPossible(allPassed)
                .checkList(items)
                .missingCourses(missingCourses)
                .build();
    }

    // 헬퍼 메서드: 이 메서드가 부족한 점수를 자동으로 계산해줍니다.
    private boolean addCheckItem(List<CheckItem> items, String category, int current, int required) {
        boolean passed = current >= required;
        items.add(CheckItem.builder()
                .category(category)
                .current(current)
                .required(required)
                .isPassed(passed)
                // ▼ 여기가 부족한 점수를 보여주는 핵심 부분입니다.
                .message(passed ? "통과" : (current - required) + " (부족)")
                .build());
        return passed;
    }
}