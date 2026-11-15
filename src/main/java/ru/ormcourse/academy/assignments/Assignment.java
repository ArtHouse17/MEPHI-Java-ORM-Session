package ru.ormcourse.academy.assignments;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import ru.ormcourse.academy.catalog.Lesson;
import ru.ormcourse.academy.common.IdentifiedEntity;

@Entity
@Table(name = "assignment")
public class Assignment extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 6000)
    private String description;

    @Column(nullable = false)
    private Instant dueAt;

    @Column(nullable = false)
    private int maxScore;

    protected Assignment() {
    }

    public Assignment(Lesson lesson, String title, String description, Instant dueAt, int maxScore) {
        this.lesson = lesson;
        this.title = title;
        this.description = description;
        this.dueAt = dueAt;
        this.maxScore = maxScore;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void update(String title, String description, Instant dueAt, int maxScore) {
        this.title = title;
        this.description = description;
        this.dueAt = dueAt;
        this.maxScore = maxScore;
    }
}
