package ru.ormcourse.academy.service;

import java.time.Clock;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ormcourse.academy.dto.ActivityDtos.EnrollmentView;
import ru.ormcourse.academy.dto.ActivityDtos.ReviewInput;
import ru.ormcourse.academy.dto.ActivityDtos.ReviewView;
import ru.ormcourse.academy.entity.Course;
import ru.ormcourse.academy.entity.CourseReview;
import ru.ormcourse.academy.entity.Enrollment;
import ru.ormcourse.academy.entity.Role;
import ru.ormcourse.academy.entity.UserAccount;
import ru.ormcourse.academy.exception.ApiException;
import ru.ormcourse.academy.repository.CourseReviewRepository;
import ru.ormcourse.academy.repository.EnrollmentRepository;

@Service
@Transactional(readOnly = true)
public class CourseActivityService {
    private final CourseService courses;
    private final AccountService accounts;
    private final EnrollmentRepository enrollments;
    private final CourseReviewRepository reviews;
    private final Clock clock;

    public CourseActivityService(CourseService courses, AccountService accounts, EnrollmentRepository enrollments,
                                 CourseReviewRepository reviews, Clock clock) {
        this.courses = courses;
        this.accounts = accounts;
        this.enrollments = enrollments;
        this.reviews = reviews;
        this.clock = clock;
    }

    @Transactional
    public EnrollmentView enroll(long courseId, long studentId) {
        Course course = courses.requireCourse(courseId);
        UserAccount student = accounts.requireRole(studentId, Role.STUDENT);
        if (enrollments.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw ApiException.conflict("The student is already enrolled in this course");
        }
        return EnrollmentView.from(enrollments.save(new Enrollment(course, student, clock.instant())));
    }

    public EnrollmentView enrollment(long courseId, long studentId) {
        return EnrollmentView.from(enrollments.findOneByCourseIdAndStudentId(courseId, studentId)
                .orElseThrow(() -> ApiException.notFound("Enrollment for student", studentId)));
    }

    @Transactional
    public void withdraw(long courseId, long studentId) {
        Enrollment enrollment = enrollments.findByCourseIdAndStudentId(courseId, studentId)
                .orElseThrow(() -> ApiException.notFound("Enrollment for student", studentId));
        enrollments.delete(enrollment);
    }

    public List<EnrollmentView> courseEnrollments(long courseId) {
        courses.requireCourse(courseId);
        return enrollments.findByCourseIdOrderByIdAsc(courseId).stream().map(EnrollmentView::from).toList();
    }

    public List<EnrollmentView> studentEnrollments(long studentId) {
        accounts.requireRole(studentId, Role.STUDENT);
        return enrollments.findByStudentIdOrderByIdAsc(studentId).stream().map(EnrollmentView::from).toList();
    }

    @Transactional
    public Enrollment requireEnrollment(long courseId, long studentId) {
        return enrollments.findByCourseIdAndStudentId(courseId, studentId)
                .orElseThrow(() -> ApiException.forbidden("The student must be enrolled in the course"));
    }

    public List<ReviewView> listReviews(long courseId) {
        courses.requireCourse(courseId);
        return reviews.findByCourseIdOrderByIdAsc(courseId).stream().map(ReviewView::from).toList();
    }

    public ReviewView review(long courseId, long reviewId) {
        return ReviewView.from(requireReview(courseId, reviewId));
    }

    @Transactional
    public ReviewView createReview(long courseId, long studentId, ReviewInput input) {
        Enrollment enrollment = requireEnrollment(courseId, studentId);
        if (reviews.findByCourseIdAndStudentId(courseId, studentId).isPresent()) {
            throw ApiException.conflict("This student has already reviewed the course");
        }
        return ReviewView.from(reviews.save(new CourseReview(enrollment.getCourse(), enrollment.getStudent(),
                input.rating(), input.comment(), clock.instant())));
    }

    @Transactional
    public ReviewView updateReview(long courseId, long reviewId, long actorId, ReviewInput input) {
        CourseReview review = requireOwnReview(courseId, reviewId, actorId);
        review.update(input.rating(), input.comment());
        return ReviewView.from(review);
    }

    @Transactional
    public void deleteReview(long courseId, long reviewId, long actorId) {
        reviews.delete(requireOwnReview(courseId, reviewId, actorId));
    }

    private CourseReview requireReview(long courseId, long reviewId) {
        return ApiException.require(reviews.findById(reviewId)
                .filter(review -> review.getCourse().getId() == courseId), "Review", reviewId);
    }

    private CourseReview requireOwnReview(long courseId, long reviewId, long actorId) {
        CourseReview review = requireReview(courseId, reviewId);
        if (review.getStudent().getId() != actorId) {
            throw ApiException.forbidden("Only the review author can perform this operation");
        }
        return review;
    }
}
