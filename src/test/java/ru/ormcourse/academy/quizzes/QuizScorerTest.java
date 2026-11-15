package ru.ormcourse.academy.quizzes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.ormcourse.academy.quizzes.QuizScorer.Grade;
import ru.ormcourse.academy.quizzes.QuizScorer.QuestionKey;
import ru.ormcourse.academy.quizzes.QuizScorer.Selection;

class QuizScorerTest {
    private static final List<QuestionKey> KEY = List.of(
            new QuestionKey(10, 8, Set.of(101L, 102L), 101L),
            new QuestionKey(20, 3, Set.of(201L, 202L), 202L));

    @Test
    void addsPointsForAllCorrectAnswersInAnyOrder() {
        Grade grade = QuizScorer.grade(KEY, List.of(new Selection(20, 202), new Selection(10, 101)));
        assertThat(grade.score()).isEqualTo(11);
        assertThat(grade.maxScore()).isEqualTo(11);
        assertThat(grade.pointsByQuestion()).containsEntry(10L, 8).containsEntry(20L, 3);
    }

    @Test
    void returnsPartialScoreForIncorrectAnswer() {
        Grade grade = QuizScorer.grade(KEY, List.of(new Selection(10, 102), new Selection(20, 202)));
        assertThat(grade.score()).isEqualTo(3);
        assertThat(grade.pointsByQuestion()).containsEntry(10L, 0).containsEntry(20L, 3);
    }

    @Test
    void rejectsDuplicateAnswers() {
        assertThatIllegalArgumentException().isThrownBy(() ->
                        QuizScorer.grade(KEY, List.of(new Selection(10, 101), new Selection(10, 102))))
                .withMessageContaining("more than once");
    }

    @Test
    void rejectsMissingAnswers() {
        assertThatIllegalArgumentException().isThrownBy(() -> QuizScorer.grade(KEY, List.of(new Selection(10, 101))))
                .withMessageContaining("every question");
    }

    @Test
    void rejectsAnOptionFromAnotherQuestion() {
        assertThatIllegalArgumentException().isThrownBy(() ->
                        QuizScorer.grade(KEY, List.of(new Selection(10, 201), new Selection(20, 202))))
                .withMessageContaining("does not belong");
    }

    @Test
    void rejectsAnUnknownQuestionEvenIfTheCountMatches() {
        assertThatIllegalArgumentException().isThrownBy(() ->
                        QuizScorer.grade(KEY, List.of(new Selection(99, 101), new Selection(20, 202))))
                .withMessageContaining("do not match");
    }
}
