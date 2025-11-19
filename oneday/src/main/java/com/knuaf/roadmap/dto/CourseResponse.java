package com.knuaf.roadmap.dto; // 👈 com.example -> com.knuaf 로 수정
import com.knuaf.roadmap.domain.Course;

/**
 * 강의 목록 조회용 응답 DTO
 */
public record CourseResponse(
        Long id,
        String lecNum,
        String lecName,
        String lecType,
        Long credit,
        String professor,
        String openCollage,
        String openDepart,
        String language
) {
    public static CourseResponse from(Course entity) {
        return new CourseResponse(
                entity.getId(),
                entity.getLec_num(),
                entity.getLec_name(),
                entity.getLec_type(),
                entity.getCredit(),
                entity.getProfessor(),
                entity.getOpen_collage(),
                entity.getOpen_depart(),
                entity.getLanguage()
        );
    }
}