package ru.ormcourse.academy.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import ru.ormcourse.academy.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Enrollment> findByCourseIdAndStudentId(long courseId, long studentId);

    Optional<Enrollment> findOneByCourseIdAndStudentId(long courseId, long studentId);

    boolean existsByCourseIdAndStudentId(long courseId, long studentId);

    @EntityGraph(attributePaths = {"course", "student"})
    List<Enrollment> findByCourseIdOrderByIdAsc(long courseId);

    @EntityGraph(attributePaths = {"course", "student"})
    List<Enrollment> findByStudentIdOrderByIdAsc(long studentId);

}
