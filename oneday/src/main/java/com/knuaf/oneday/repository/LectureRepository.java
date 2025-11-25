package com.knuaf.oneday.repository;

import com.knuaf.oneday.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

}