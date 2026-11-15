package ru.ormcourse.academy.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByModuleIdOrderByPositionAscIdAsc(long moduleId);

    List<Lesson> findByModuleCourseIdOrderByPositionAscIdAsc(long courseId);

}
