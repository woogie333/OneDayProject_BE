package com.knuaf.oneday.component;

import org.springframework.stereotype.Component;

@Component
public class LectureTableNameProvider {

    private static final String TABLE_PREFIX = "lecture_list_";

    public static String getTableName(String semester) {
        // 입력값 검증 (보안상 매우 중요! 이상한 SQL 주입 방지)
        // 예: "2024-1" 형식이 맞는지 정규식으로 체크
        if (!semester.matches("^\\d{4}-\\d{1,2}$")) {
            throw new IllegalArgumentException("잘못된 학기 형식입니다. (예: 2024-1)");
        }

        // 로직: 2024-1 -> lecture_list20241
        return TABLE_PREFIX + semester.replace("-", "");
    }
}