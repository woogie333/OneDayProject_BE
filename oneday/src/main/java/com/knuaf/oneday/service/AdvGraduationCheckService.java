package com.knuaf.oneday.service;

import com.knuaf.oneday.dto.GraduationCheckResponse;
import com.knuaf.oneday.dto.GraduationCheckResponse.CheckItem;
import com.knuaf.oneday.entity.Advcomp;
import com.knuaf.oneday.entity.User;
import com.knuaf.oneday.entity.UserAttend;
import com.knuaf.oneday.repository.AdvCompRepository;
import com.knuaf.oneday.repository.UserAttendRepository; // ★ 추가
import com.knuaf.oneday.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvGraduationCheckService {

    private final UserRepository userRepository;
    private final AdvCompRepository advcompRepository;
    private final UserAttendRepository userAttendRepository; // ★ 추가: 수강 내역 조회용

    // 2021~2025학번 기준 상수 정의
    private static final int REQ_TOTAL_CREDIT = 140;
    private static final int REQ_ABEEK_GENERAL = 15;
    private static final int REQ_BASE_MAJOR = 18;
    private static final int REQ_ENGIN_MAJOR = 60;
    private static final int REQ_ABEEK_TOTAL = 93;
    private static final int REQ_TOEIC = 700;

    // ★ 추가: 심화컴퓨터 필수 과목 리스트 (과목코드, 과목명)
    // ※ 학교 커리큘럼에 맞춰 코드를 정확히 수정해야 합니다.
    private static final Map<String, String> REQUIRED_COURSES = new LinkedHashMap<>() {{
        put("CLTR0819", "기초수학2");
        put("COME0301", "이산수학");
        put("COMP0204", "프로그래밍기초");
        put("COMP0205", "기초창의공학설계");
        put("COME0331", "자료구조");
        put("COMP0217", "자바프로그래밍");
        put("COMP0411", "컴퓨터구조");
        put("ELEC0462", "시스템프로그래밍");
        put("COMP0312", "운영체제");
        put("COMP0319", "알고리즘");
        put("ITEC0401", "종합설계프로젝트1");
        put("ITEC0402", "종합설계프로젝트2");
        // 필요한 과목 계속 추가...
    }};

    @Transactional(readOnly = true)
    public GraduationCheckResponse checkGraduation(Long studentId) {

        // 1. 사용자 정보 가져오기
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        Advcomp adv = advcompRepository.findByUser_StudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("심화컴퓨터 이수 정보를 찾을 수 없습니다."));

        List<CheckItem> items = new ArrayList<>();
        boolean allPassed = true;

        // 2. [기존] 학점 및 점수 검증 로직

        // (1) 총 이수학점
        int currentTotal = user.getTotal_credit() != null ? user.getTotal_credit().intValue() : 0;
        if (!addCheckItem(items, "총 이수학점", currentTotal, REQ_TOTAL_CREDIT)) allPassed = false;

        // (2) 기본소양
        int currentGeneral = adv.getAbeek_general() != null ? adv.getAbeek_general() : 0;
        if (!addCheckItem(items, "기본소양(교양)", currentGeneral, REQ_ABEEK_GENERAL)) allPassed = false;

        // (3) 전공기반
        int currentBase = adv.getBase_major() != null ? adv.getBase_major() : 0;
        if (!addCheckItem(items, "전공기반", currentBase, REQ_BASE_MAJOR)) allPassed = false;

        // (4) 공학전공
        int currentEngin = adv.getEngin_major() != null ? adv.getEngin_major() : 0;
        if (!addCheckItem(items, "공학전공", currentEngin, REQ_ENGIN_MAJOR)) allPassed = false;

        // (5) ABEEK 총점
        int currentAbeekTotal = adv.getAbeek_total() != null ? adv.getAbeek_total() : 0;
        if (!addCheckItem(items, "ABEEK 총점", currentAbeekTotal, REQ_ABEEK_TOTAL)) allPassed = false;

        // (6) 영어 성적
        int currentEngScore = user.getEng_score() != null ? user.getEng_score().intValue() : 0;
        if (!addCheckItem(items, "영어 성적(토익)", currentEngScore, REQ_TOEIC)) allPassed = false;

        // (7) 현장실습
        boolean isInternshipDone = user.isInternship();
        items.add(CheckItem.builder()
                .category("현장실습")
                .current(isInternshipDone ? 1 : 0)
                .required(1)
                .isPassed(isInternshipDone)
                .message(isInternshipDone ? "이수 완료" : "미이수")
                .build());
        if (!isInternshipDone) allPassed = false;

        // ----------------------------------------------------
        // 3. [추가] 필수 과목 이수 여부 체크
        // ----------------------------------------------------

        // 3-1. 학생이 들은 모든 과목의 코드를 가져옴 (Set으로 조회 속도 향상)
        List<UserAttend> takenLectures = userAttendRepository.findByStudentId(studentId);

        Set<String> takenCodes = takenLectures.stream()
                .map(UserAttend::getLecId) // 과목번호(lec_num)와 매핑된 ID
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toSet());

        // 3-2. 필수 과목 Map을 순회하며 안 들은 과목 찾기
        List<String> missingCourses = new ArrayList<>();

        for (Map.Entry<String, String> entry : REQUIRED_COURSES.entrySet()) {
            String code = entry.getKey();
            String name = entry.getValue();

            if (!takenCodes.contains(code)) {
                // 이름과 코드를 같이 저장 (예: "자료구조 (COMP0205)")
                missingCourses.add(name + " (" + code + ")");
            }
        }

        // 3-3. 결과 반영
        boolean passCourses = missingCourses.isEmpty();

        items.add(CheckItem.builder()
                .category("필수과목 이수")
                .current(REQUIRED_COURSES.size() - missingCourses.size()) // 이수 과목 수
                .required(REQUIRED_COURSES.size()) // 전체 필수 수
                .isPassed(passCourses)
                .message(passCourses ? "모두 이수함" : missingCourses.size() + "과목 미이수")
                .build());

        if (!passCourses) allPassed = false;

        // 4. 결과 반환
        return GraduationCheckResponse.builder()
                .studentId(studentId)
                .isGraduationPossible(allPassed)
                .checkList(items)
                .missingCourses(missingCourses) // ★ 부족한 과목 리스트 추가
                .build();
    }

    // 헬퍼 메서드: 코드 중복 제거를 위해 리팩토링
    private boolean addCheckItem(List<CheckItem> items, String category, int current, int required) {
        boolean passed = current >= required;
        items.add(CheckItem.builder()
                .category(category)
                .current(current)
                .required(required)
                .isPassed(passed)
                .message(passed ? "통과" : (current - required) + " (부족)")
                .build());
        return passed;
    }
}