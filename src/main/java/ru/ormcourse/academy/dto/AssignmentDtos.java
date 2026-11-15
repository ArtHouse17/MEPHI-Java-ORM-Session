package ru.ormcourse.academy.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import ru.ormcourse.academy.entity.Assignment;
import ru.ormcourse.academy.entity.Submission;

public final class AssignmentDtos {
    private AssignmentDtos() {
    }

    public record AssignmentInput(
            @NotBlank @Size(max = 180) String title,
            @NotBlank @Size(max = 6000) String description,
            @NotNull Instant dueAt,
            @NotNull @Min(1) @Max(1000) Integer maxScore) {
    }

    public record SubmissionInput(@NotBlank @Size(max = 10000) String content) {
    }

    public record GradeInput(@NotNull @Min(0) @Max(1000) Integer score, @Size(max = 4000) String feedback) {
    }

    public record AssignmentView(long id, long lessonId, String title, String description,
                                 Instant dueAt, int maxScore) {
        public static AssignmentView from(Assignment assignment) {
            return new AssignmentView(assignment.getId(), assignment.getLesson().getId(), assignment.getTitle(),
                    assignment.getDescription(), assignment.getDueAt(), assignment.getMaxScore());
        }
    }

    public record SubmissionView(long id, long assignmentId, long studentId, Instant submittedAt,
                                 String content, Integer score, String feedback) {
        public static SubmissionView from(Submission submission) {
            return new SubmissionView(submission.getId(), submission.getAssignment().getId(),
                    submission.getStudent().getId(), submission.getSubmittedAt(), submission.getContent(),
                    submission.getScore(), submission.getFeedback());
        }
    }
}
