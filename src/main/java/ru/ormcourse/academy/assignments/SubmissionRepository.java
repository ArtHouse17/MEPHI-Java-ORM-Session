package ru.ormcourse.academy.assignments;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByAssignmentIdAndStudentId(long assignmentId, long studentId);

    boolean existsByAssignmentIdAndScoreGreaterThan(long assignmentId, int score);

    List<Submission> findByAssignmentIdOrderByIdAsc(long assignmentId);

}
