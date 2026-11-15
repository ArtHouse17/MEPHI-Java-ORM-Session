package ru.ormcourse.academy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ormcourse.academy.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizIdOrderByPositionAscIdAsc(long quizId);

    long countByQuizId(long quizId);

}
