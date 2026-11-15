package ru.ormcourse.academy.quizzes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import ru.ormcourse.academy.catalog.CourseModule;
import ru.ormcourse.academy.common.IdentifiedEntity;

@Entity
@Table(name = "quiz")
public class Quiz extends IdentifiedEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false, unique = true)
    private CourseModule module;

    @Column(nullable = false, length = 180)
    private String title;

    protected Quiz() {
    }

    public Quiz(CourseModule module, String title) {
        this.module = module;
        this.title = title;
    }

    public CourseModule getModule() {
        return module;
    }

    public String getTitle() {
        return title;
    }

    public void update(String title) {
        this.title = title;
    }
}
