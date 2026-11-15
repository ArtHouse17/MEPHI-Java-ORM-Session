package ru.ormcourse.academy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ormcourse.academy.entity.LessonResource;

public interface LessonResourceRepository extends JpaRepository<LessonResource, Long> {
    List<LessonResource> findByLessonIdOrderByIdAsc(long lessonId);

}
