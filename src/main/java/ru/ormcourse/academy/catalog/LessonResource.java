package ru.ormcourse.academy.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.ormcourse.academy.common.IdentifiedEntity;

@Entity
@Table(name = "lesson_resource")
public class LessonResource extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 1000)
    private String url;

    protected LessonResource() {
    }

    public LessonResource(Lesson lesson, String title, String url) {
        this.lesson = lesson;
        this.title = title;
        this.url = url;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void update(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
