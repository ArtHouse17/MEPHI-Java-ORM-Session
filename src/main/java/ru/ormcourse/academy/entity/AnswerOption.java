package ru.ormcourse.academy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "answer_option")
public class AnswerOption extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = 600)
    private String text;

    @Column(nullable = false)
    private boolean correct;

    @Column(nullable = false)
    private int position;

    protected AnswerOption() {
    }

    public AnswerOption(Question question, String text, boolean correct, int position) {
        this.question = question;
        this.text = text;
        this.correct = correct;
        this.position = position;
    }

    public Question getQuestion() {
        return question;
    }

    public String getText() {
        return text;
    }

    public boolean getCorrect() {
        return correct;
    }

    public int getPosition() {
        return position;
    }

    public void update(String text, boolean correct, int position) {
        this.text = text;
        this.correct = correct;
        this.position = position;
    }
}
