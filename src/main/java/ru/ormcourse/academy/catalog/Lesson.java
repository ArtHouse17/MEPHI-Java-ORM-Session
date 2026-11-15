package ru.ormcourse.academy.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.ormcourse.academy.common.IdentifiedEntity;

@Entity
@Table(name = "lesson")
public class Lesson extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule module;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 12000)
    private String content;

    @Column(nullable = false)
    private int position;

    protected Lesson() {
    }

    public Lesson(CourseModule module, String title, String content, int position) {
        this.module = module;
        this.title = title;
        this.content = content;
        this.position = position;
    }

    public CourseModule getModule() {
        return module;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getPosition() {
        return position;
    }

    public void update(String title, String content, int position) {
        this.title = title;
        this.content = content;
        this.position = position;
    }
}
