package ru.ormcourse.academy.quizzes;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    List<AnswerOption> findByQuestionQuizIdOrderByPositionAscIdAsc(long quizId);

    List<AnswerOption> findByQuestionIdOrderByPositionAscIdAsc(long questionId);

}
