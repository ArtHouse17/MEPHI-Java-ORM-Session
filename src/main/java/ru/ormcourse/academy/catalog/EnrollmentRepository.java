package ru.ormcourse.academy.catalog;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

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
