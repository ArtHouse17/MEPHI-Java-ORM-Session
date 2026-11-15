package ru.ormcourse.academy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "enrollment")
public class Enrollment extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private UserAccount student;

    @Column(nullable = false)
    private Instant enrolledAt;

    protected Enrollment() {
    }

    public Enrollment(Course course, UserAccount student, Instant enrolledAt) {
        this.course = course;
        this.student = student;
        this.enrolledAt = enrolledAt;
    }

    public Course getCourse() {
        return course;
    }

    public UserAccount getStudent() {
        return student;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }
}
