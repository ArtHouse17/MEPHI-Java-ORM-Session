package ru.ormcourse.academy.quizzes;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findBySubmissionIdOrderByIdAsc(long submissionId);

}
