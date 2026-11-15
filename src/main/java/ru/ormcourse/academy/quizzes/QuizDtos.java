package ru.ormcourse.academy.quizzes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public final class QuizDtos {
    public static final int MAX_QUESTIONS = 100;

    private QuizDtos() {
    }

    public record QuizInput(@NotBlank @Size(max = 180) String title) {
    }

    public record QuestionInput(
            @NotBlank @Size(max = 1200) String text,
            @NotNull @Positive @Max(1000) Integer points,
            @NotNull @Positive Integer position) {
    }

    public record OptionInput(
            @NotBlank @Size(max = 600) String text,
            @NotNull Boolean correct,
            @NotNull @Positive Integer position) {
    }

    public record PickInput(@NotNull @Positive Long questionId, @NotNull @Positive Long optionId) {
    }

    public record AttemptInput(@NotEmpty @Size(max = MAX_QUESTIONS) List<@NotNull @Valid PickInput> answers) {
    }

    public record QuizView(long id, long moduleId, String title) {
        static QuizView from(Quiz quiz) {
            return new QuizView(quiz.getId(), quiz.getModule().getId(), quiz.getTitle());
        }
    }

    public record QuestionView(long id, long quizId, String text, int points, int position) {
        static QuestionView from(Question question) {
            return new QuestionView(question.getId(), question.getQuiz().getId(), question.getText(),
                    question.getPoints(), question.getPosition());
        }
    }

    public record OptionView(long id, long questionId, String text, boolean correct, int position) {
        static OptionView from(AnswerOption option) {
            return new OptionView(option.getId(), option.getQuestion().getId(), option.getText(),
                    option.getCorrect(), option.getPosition());
        }
    }

    public record OptionChoice(long id, String text, int position) {
        static OptionChoice from(AnswerOption option) {
            return new OptionChoice(option.getId(), option.getText(), option.getPosition());
        }
    }

    public record QuizQuestion(QuestionView question, List<OptionChoice> options) {
    }

    public record QuizPaper(QuizView quiz, List<QuizQuestion> questions) {
    }

    public record AttemptSummary(long id, long quizId, long studentId, Instant takenAt, int score, int maxScore) {
        static AttemptSummary from(QuizSubmission attempt) {
            return new AttemptSummary(attempt.getId(), attempt.getQuiz().getId(), attempt.getStudent().getId(),
                    attempt.getTakenAt(), attempt.getScore(), attempt.getMaxScore());
        }
    }

    public record AnswerView(long questionId, long optionId, int pointsAwarded) {
        static AnswerView from(QuizAnswer answer) {
            return new AnswerView(answer.getQuestion().getId(), answer.getSelectedOption().getId(),
                    answer.getPointsAwarded());
        }
    }

    public record AttemptView(AttemptSummary attempt, List<AnswerView> answers) {
    }
}
