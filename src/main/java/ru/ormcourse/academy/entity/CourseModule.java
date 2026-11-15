package ru.ormcourse.academy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_module")
public class CourseModule extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false)
    private int position;

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    @OrderBy("position, id")
    private List<Lesson> lessons = new ArrayList<>();

    protected CourseModule() {
    }

    public CourseModule(Course course, String title, String description, int position) {
        this.course = course;
        this.title = title;
        this.description = description;
        this.position = position;
    }

    public Course getCourse() {
        return course;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPosition() {
        return position;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void update(String title, String description, int position) {
        this.title = title;
        this.description = description;
        this.position = position;
    }
}
