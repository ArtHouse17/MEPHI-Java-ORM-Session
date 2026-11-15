package ru.ormcourse.academy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "lesson_session")
public class LessonSession extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false)
    private Instant startsAt;

    @Column(nullable = false)
    private Instant endsAt;

    @Column(nullable = false, length = 500)
    private String location;

    protected LessonSession() {
    }

    public LessonSession(Lesson lesson, Instant startsAt, Instant endsAt, String location) {
        this.lesson = lesson;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.location = location;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public String getLocation() {
        return location;
    }

    public void update(Instant startsAt, Instant endsAt, String location) {
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.location = location;
    }
}
