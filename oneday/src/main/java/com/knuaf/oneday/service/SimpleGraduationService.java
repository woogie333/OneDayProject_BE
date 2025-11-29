package com.knuaf.oneday.service;

import com.knuaf.oneday.dto.SimpleGraduationResponse;
import com.knuaf.oneday.entity.User;
import com.knuaf.oneday.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SimpleGraduationService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public SimpleGraduationResponse getSimpleStatus(String userId) {

        // 1. User 정보 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

        // 2. 현재 내 점수 가져오기 (Null 방지)
        int myTotal = user.getTotal_credit() != null ? user.getTotal_credit().intValue() : 0;
        int myMajor = user.getMajor_credit() != null ? user.getMajor_credit().intValue() : 0;
        int myGeneral = user.getGeneral_credit() != null ? user.getGeneral_credit().intValue() : 0;

        long studentId = user.getStudentId();
        int entryYear = (int) (studentId / 1000000); // 학번 앞 4자리 (입학년도)
        String major = user.getMajor();
        if (major == null) major = "";

        // --------------------------------------------------------
        // 3. 목표 기준(Target) 설정
        // --------------------------------------------------------

        int targetTotal = 140;   // 졸업 기준 총 학점
        int targetMajor = 0;     // 기준 전공 학점
        int targetGeneral = 30;  // 기준 교양 학점 (최소)

        // [A] 공통 교양 기준 (학번별 최소 기준)
        if (entryYear <= 2017) {
            targetGeneral = 30;
        } else if (entryYear <= 2022) {
            // 2018~2022학번: 24~42학점 -> 부족 여부는 '최소(24)' 기준으로 판단
            targetGeneral = 24;
        } else {
            // 2023학번 ~
            targetGeneral = 30;
        }

        // [B] 전공별 총학점 & 전공학점 기준
        if (major.contains("글로벌") || major.contains("Global")) {
            // [글로벌SW융합전공]
            targetTotal = 130;
            targetMajor = 51;

        } else if (major.contains("인공지능") || major.contains("AI")) {
            // [인공지능컴퓨팅전공]
            targetTotal = 140;
            targetMajor = 72;

        } else if (major.contains("심화") || major.contains("Computer") || major.contains("플랫폼")) {
            // [심화컴퓨터공학] (플랫폼SW 포함 시)
            if (entryYear >= 2021) {
                targetTotal = 140;
                targetMajor = 78; // 전공기반(18) + 공학전공(60)
            } else if (entryYear >= 2012) {
                targetTotal = 150;
                targetMajor = 96; // 전공기반(21) + 공학전공(75)
            } else {
                targetTotal = 140;
                targetMajor = 78;
            }
        }

        // --------------------------------------------------------
        // 4. 부족한 학점 계산 (음수 방지)
        // --------------------------------------------------------
        int missingMajor = Math.max(0, targetMajor - myMajor);
        int missingGeneral = Math.max(0, targetGeneral - myGeneral);

        // 5. 결과 반환
        return SimpleGraduationResponse.builder()
                .studentId(studentId)
                .majorName(major)
                .currentTotal(myTotal)
                .requiredTotal(targetTotal)
                .missingMajor(missingMajor)     // 부족한 전공
                .missingGeneral(missingGeneral) // 부족한 교양
                .build();
    }
}