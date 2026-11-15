package ru.ormcourse.academy.quizzes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import ru.ormcourse.academy.accounts.UserAccount;
import ru.ormcourse.academy.common.IdentifiedEntity;

@Entity
@Table(name = "quiz_submission")
public class QuizSubmission extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private UserAccount student;

    @Column(nullable = false)
    private Instant takenAt;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int maxScore;

    protected QuizSubmission() {
    }

    public QuizSubmission(Quiz quiz, UserAccount student, Instant takenAt, int score, int maxScore) {
        this.quiz = quiz;
        this.student = student;
        this.takenAt = takenAt;
        this.score = score;
        this.maxScore = maxScore;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public UserAccount getStudent() {
        return student;
    }

    public Instant getTakenAt() {
        return takenAt;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }
}
