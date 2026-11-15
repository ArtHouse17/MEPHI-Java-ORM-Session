package ru.ormcourse.academy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "course_review")
public class CourseReview extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private UserAccount student;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 4000)
    private String comment;

    @Column(nullable = false)
    private Instant createdAt;

    protected CourseReview() {
    }

    public CourseReview(Course course, UserAccount student, int rating, String comment, Instant createdAt) {
        this.course = course;
        this.student = student;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Course getCourse() {
        return course;
    }

    public UserAccount getStudent() {
        return student;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void update(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
