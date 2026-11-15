package ru.ormcourse.academy.assignments;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import ru.ormcourse.academy.accounts.UserAccount;
import ru.ormcourse.academy.common.IdentifiedEntity;

@Entity
@Table(name = "submission")
public class Submission extends IdentifiedEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private UserAccount student;

    @Column(nullable = false)
    private Instant submittedAt;

    @Column(nullable = false, length = 10000)
    private String content;

    @Column
    private Integer score;

    @Column(length = 4000)
    private String feedback;

    protected Submission() {
    }

    public Submission(Assignment assignment, UserAccount student, Instant submittedAt, String content) {
        this.assignment = assignment;
        this.student = student;
        this.submittedAt = submittedAt;
        this.content = content;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public UserAccount getStudent() {
        return student;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public String getContent() {
        return content;
    }

    public Integer getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void grade(int score, String feedback) {
        this.score = score;
        this.feedback = feedback;
    }
}
