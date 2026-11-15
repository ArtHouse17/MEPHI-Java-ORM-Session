package ru.ormcourse.academy.quizzes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.ormcourse.academy.common.IdentifiedEntity;

@Entity
@Table(name = "question")
public class Question extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, length = 1200)
    private String text;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private int position;

    protected Question() {
    }

    public Question(Quiz quiz, String text, int points, int position) {
        this.quiz = quiz;
        this.text = text;
        this.points = points;
        this.position = position;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public String getText() {
        return text;
    }

    public int getPoints() {
        return points;
    }

    public int getPosition() {
        return position;
    }

    public void update(String text, int points, int position) {
        this.text = text;
        this.points = points;
        this.position = position;
    }
}
