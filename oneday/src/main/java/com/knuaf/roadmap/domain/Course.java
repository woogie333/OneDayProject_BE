package com.knuaf.roadmap.domain;

import jakarta.persistence.*; //java 기반에서 DB를 쉽게 사용할 수 있는 API 인터페이스
import lombok.*; //어노테이션(주석) 기반으로 코드 자동완성 라이브러리.

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "appdb")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String lec_num;

    @Column(nullable = false)
    private Long credit;

    @Column(nullable = false)
    private String lec_name;

    @Column(nullable = false)
    private Long grade;

    @Column(nullable = false)
    private String lec_type;

    @Column(nullable = false)
    private String open_collage;

    @Column(nullable = false)
    private String open_depart;

    @Column(nullable = false)
    private String professor;

    @Column(nullable = false)
    private String language;

    @Builder
    public Course(String lec_num, Long credit, String lec_name, Long grade, String lec_type, String open_collage, String open_depart, String professor, String language) {
        this.lec_num = lec_num;
        this.credit = credit;
        this.lec_name = lec_name;
        this.grade = grade;
        this.lec_type = lec_type;
        this.open_collage = open_collage;
        this.open_depart = open_depart;
        this.professor = professor;
        this.language = language;
    }

    public void update(String lec_num, Long credit, String lec_name, Long grade, String lec_type, String open_collage, String open_depart, String professor, String language) {
        this.lec_num = lec_num;
        this.credit = credit;
        this.lec_name = lec_name;
        this.grade = grade;
        this.lec_type = lec_type;
        this.open_collage = open_collage;
        this.open_depart = open_depart;
        this.professor = professor;
        this.language = language;
    }

}