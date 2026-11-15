package ru.ormcourse.academy.catalog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.ormcourse.academy.catalog.ActivityDtos.EnrollmentInput;
import ru.ormcourse.academy.catalog.ActivityDtos.EnrollmentView;
import ru.ormcourse.academy.catalog.ActivityDtos.ReviewInput;
import ru.ormcourse.academy.catalog.ActivityDtos.ReviewView;
import ru.ormcourse.academy.common.Responses;

@RestController
@RequestMapping("/api")
public class CourseActivityController {
    private final CourseActivityService activity;

    public CourseActivityController(CourseActivityService activity) {
        this.activity = activity;
    }

    @GetMapping("/courses/{courseId}/enrollments")
    List<EnrollmentView> courseEnrollments(@PathVariable @Positive long courseId) {
        return activity.courseEnrollments(courseId);
    }

    @GetMapping("/users/{studentId}/enrollments")
    List<EnrollmentView> studentEnrollments(@PathVariable @Positive long studentId) {
        return activity.studentEnrollments(studentId);
    }

    @GetMapping("/courses/{courseId}/enrollments/{studentId}")
    EnrollmentView enrollment(@PathVariable @Positive long courseId, @PathVariable @Positive long studentId) {
        return activity.enrollment(courseId, studentId);
    }

    @PostMapping("/courses/{courseId}/enrollments")
    ResponseEntity<EnrollmentView> enroll(@PathVariable @Positive long courseId,
                                          @Valid @RequestBody EnrollmentInput input) {
        EnrollmentView created = activity.enroll(courseId, input.studentId());
        return Responses.created(created.studentId(), created);
    }

    @DeleteMapping("/courses/{courseId}/enrollments/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void withdraw(@PathVariable @Positive long courseId, @PathVariable @Positive long studentId) {
        activity.withdraw(courseId, studentId);
    }

    @GetMapping("/courses/{courseId}/reviews")
    List<ReviewView> reviews(@PathVariable @Positive long courseId) {
        return activity.listReviews(courseId);
    }

    @GetMapping("/courses/{courseId}/reviews/{reviewId}")
    ReviewView review(@PathVariable @Positive long courseId, @PathVariable @Positive long reviewId) {
        return activity.review(courseId, reviewId);
    }

    @PostMapping("/courses/{courseId}/reviews")
    ResponseEntity<ReviewView> createReview(@PathVariable @Positive long courseId,
                                            @RequestHeader("X-Actor-Id") @Positive long studentId,
                                            @Valid @RequestBody ReviewInput input) {
        ReviewView created = activity.createReview(courseId, studentId, input);
        return Responses.created(created.id(), created);
    }

    @PutMapping("/courses/{courseId}/reviews/{reviewId}")
    ReviewView updateReview(@PathVariable @Positive long courseId, @PathVariable @Positive long reviewId,
                            @RequestHeader("X-Actor-Id") @Positive long actorId,
                            @Valid @RequestBody ReviewInput input) {
        return activity.updateReview(courseId, reviewId, actorId, input);
    }

    @DeleteMapping("/courses/{courseId}/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteReview(@PathVariable @Positive long courseId, @PathVariable @Positive long reviewId,
                      @RequestHeader("X-Actor-Id") @Positive long actorId) {
        activity.deleteReview(courseId, reviewId, actorId);
    }
}
