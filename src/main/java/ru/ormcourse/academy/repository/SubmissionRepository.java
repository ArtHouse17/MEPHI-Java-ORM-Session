package ru.ormcourse.academy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ormcourse.academy.entity.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByAssignmentIdAndStudentId(long assignmentId, long studentId);

    boolean existsByAssignmentIdAndScoreGreaterThan(long assignmentId, int score);

    List<Submission> findByAssignmentIdOrderByIdAsc(long assignmentId);

    List<Submission> findByStudentIdOrderBySubmittedAtDescIdDesc(long studentId);
}
