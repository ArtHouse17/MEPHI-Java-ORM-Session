package ru.ormcourse.academy.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import ru.ormcourse.academy.entity.CourseReview;
import ru.ormcourse.academy.entity.Enrollment;

public final class ActivityDtos {
    private ActivityDtos() {
    }

    public record EnrollmentInput(@NotNull @Positive Long studentId) {
    }

    public record EnrollmentView(long courseId, String course, long studentId, String student, Instant enrolledAt) {
        public static EnrollmentView from(Enrollment enrollment) {
            return new EnrollmentView(enrollment.getCourse().getId(), enrollment.getCourse().getTitle(),
                    enrollment.getStudent().getId(), enrollment.getStudent().getName(), enrollment.getEnrolledAt());
        }
    }

    public record ReviewInput(@NotNull @Min(1) @Max(5) Integer rating,
                              @NotBlank @Size(max = 4000) String comment) {
    }

    public record ReviewView(long id, long courseId, long studentId, int rating, String comment, Instant createdAt) {
        public static ReviewView from(CourseReview review) {
            return new ReviewView(review.getId(), review.getCourse().getId(), review.getStudent().getId(),
                    review.getRating(), review.getComment(), review.getCreatedAt());
        }
    }
}
