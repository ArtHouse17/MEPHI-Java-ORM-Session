package ru.ormcourse.academy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "quiz_answer")
public class QuizAnswer extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private QuizSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "selected_option_id", nullable = false)
    private AnswerOption selectedOption;

    @Column(nullable = false)
    private int pointsAwarded;

    protected QuizAnswer() {
    }

    public QuizAnswer(QuizSubmission submission, Question question, AnswerOption selectedOption, int pointsAwarded) {
        this.submission = submission;
        this.question = question;
        this.selectedOption = selectedOption;
        this.pointsAwarded = pointsAwarded;
    }

    public QuizSubmission getSubmission() {
        return submission;
    }

    public Question getQuestion() {
        return question;
    }

    public AnswerOption getSelectedOption() {
        return selectedOption;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }
}
