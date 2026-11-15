package ru.ormcourse.academy.quizzes;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizIdOrderByPositionAscIdAsc(long quizId);

    long countByQuizId(long quizId);

}
