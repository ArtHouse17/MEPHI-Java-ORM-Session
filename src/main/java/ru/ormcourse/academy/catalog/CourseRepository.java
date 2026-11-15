package ru.ormcourse.academy.catalog;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @EntityGraph(attributePaths = {"category", "teacher"})
    List<Course> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = {"category", "teacher"})
    List<Course> findDistinctByTagsIdOrderByIdAsc(long tagId);

    @EntityGraph(attributePaths = {"category", "teacher", "modules"})
    Optional<Course> findCurriculumById(long id);

    @EntityGraph(attributePaths = "tags")
    Optional<Course> findWithTagsById(long id);

}
