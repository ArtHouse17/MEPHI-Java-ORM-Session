package ru.ormcourse.academy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ormcourse.academy.entity.QuizAnswer;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findBySubmissionIdOrderByIdAsc(long submissionId);

}
