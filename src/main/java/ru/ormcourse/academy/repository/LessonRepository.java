package ru.ormcourse.academy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ormcourse.academy.entity.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByModuleIdOrderByPositionAscIdAsc(long moduleId);

    List<Lesson> findByModuleCourseIdOrderByPositionAscIdAsc(long courseId);

}
