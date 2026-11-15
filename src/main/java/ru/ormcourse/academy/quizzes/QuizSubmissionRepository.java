package ru.ormcourse.academy.quizzes;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    boolean existsByQuizId(long quizId);

    boolean existsByQuizIdAndStudentId(long quizId, long studentId);

    Optional<QuizSubmission> findByQuizIdAndStudentId(long quizId, long studentId);

    List<QuizSubmission> findByQuizIdOrderByIdAsc(long quizId);

}
