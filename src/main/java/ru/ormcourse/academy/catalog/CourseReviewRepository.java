package ru.ormcourse.academy.catalog;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    List<CourseReview> findByCourseIdOrderByIdAsc(long courseId);

    Optional<CourseReview> findByCourseIdAndStudentId(long courseId, long studentId);

}
