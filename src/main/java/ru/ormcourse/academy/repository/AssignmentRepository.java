package ru.ormcourse.academy.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.ormcourse.academy.entity.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByLessonIdOrderByIdAsc(long lessonId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Assignment e where e.id = :id")
    Optional<Assignment> findForUpdate(long id);

}
