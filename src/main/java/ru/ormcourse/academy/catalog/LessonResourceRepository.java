package ru.ormcourse.academy.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonResourceRepository extends JpaRepository<LessonResource, Long> {
    List<LessonResource> findByLessonIdOrderByIdAsc(long lessonId);

}
