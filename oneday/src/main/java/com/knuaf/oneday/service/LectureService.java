package com.knuaf.oneday.service;

import com.knuaf.oneday.component.LectureTableNameProvider; // ★ 아까 만든 객체 import
import com.knuaf.oneday.dto.LectureResponseDto;
import com.knuaf.oneday.entity.Lecture;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final EntityManager em; // ★ Native Query를 날리기 위한 매니저
    private final LectureTableNameProvider tableNameProvider; // ★ 테이블 이름 생성기

    @Transactional(readOnly = true)
    public List<LectureResponseDto> getLectureList(String semester, String keyword) {

        // 1. Provider를 통해 안전하게 테이블 이름 가져오기
        // (예: "2024-1" -> "lecture_list20241")
        String tableName = tableNameProvider.getTableName(semester);

        // 2. 동적 SQL 생성
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(tableName); // 테이블 이름 삽입
        sql.append(" WHERE 1=1 "); // 조건 추가를 위한 더미 조건

        // 검색어가 있으면 SQL에 조건 추가
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND lec_name LIKE :keyword ");
        }

        try {
            // 3. 네이티브 쿼리 생성
            // (Lecture.class를 넣으면 결과를 자동으로 엔티티로 매핑해줍니다)
            Query query = em.createNativeQuery(sql.toString(), Lecture.class);

            // 4. 파라미터 바인딩
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword + "%");
            }

            // 5. 실행 및 결과 가져오기
            List<Lecture> resultList = query.getResultList();

            // 6. DTO로 변환하여 반환
            return resultList.stream()
                    .map(LectureResponseDto::from)
                    .toList(); // Java 16+

        } catch (Exception e) {
            // 해당 학기의 테이블이 없는 경우 (SQL 에러 발생 시) 빈 리스트 반환
            return Collections.emptyList();
        }
    }
}