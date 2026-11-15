package ru.ormcourse.academy.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonSessionRepository extends JpaRepository<LessonSession, Long> {
    @EntityGraph(attributePaths = "lesson")
    List<LessonSession> findByLessonIdOrderByStartsAtAscIdAsc(long lessonId);

    @EntityGraph(attributePaths = "lesson")
    List<LessonSession> findByLessonModuleCourseIdOrderByStartsAtAscIdAsc(long courseId);

}
