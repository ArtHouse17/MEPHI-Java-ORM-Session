package ru.ormcourse.academy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ormcourse.academy.entity.AnswerOption;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    List<AnswerOption> findByQuestionQuizIdOrderByPositionAscIdAsc(long quizId);

    List<AnswerOption> findByQuestionIdOrderByPositionAscIdAsc(long questionId);

}
