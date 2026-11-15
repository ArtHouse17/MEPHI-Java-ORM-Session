package ru.ormcourse.academy.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {
    List<CourseModule> findByCourseIdOrderByPositionAscIdAsc(long courseId);

}
