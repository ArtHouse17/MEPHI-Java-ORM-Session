package ru.ormcourse.academy.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ormcourse.academy.dto.AssignmentDtos.GradeInput;
import ru.ormcourse.academy.dto.AssignmentDtos.SubmissionInput;
import ru.ormcourse.academy.dto.AssignmentDtos.SubmissionView;
import ru.ormcourse.academy.entity.Assignment;
import ru.ormcourse.academy.entity.Enrollment;
import ru.ormcourse.academy.entity.Submission;
import ru.ormcourse.academy.exception.ApiException;
import ru.ormcourse.academy.repository.SubmissionRepository;

@Service
@Transactional(readOnly = true)
public class SubmissionService {
    private final AssignmentService assignments;
    private final SubmissionRepository submissions;
    private final CourseService courses;
    private final CourseActivityService activity;
    private final Clock clock;

    public SubmissionService(AssignmentService assignments, SubmissionRepository submissions,
                             CourseService courses, CourseActivityService activity, Clock clock) {
        this.assignments = assignments;
        this.submissions = submissions;
        this.courses = courses;
        this.activity = activity;
        this.clock = clock;
    }

    @Transactional
    public SubmissionView submit(long id, long studentId, SubmissionInput input) {
        Assignment assignment = assignments.requireLockedAssignment(id);
        Enrollment enrollment = activity.requireEnrollment(assignments.courseOf(assignment).getId(), studentId);
        if (submissions.existsByAssignmentIdAndStudentId(id, studentId)) {
            throw ApiException.conflict("The student already submitted this assignment");
        }
        Instant now = clock.instant();
        if (now.isAfter(assignment.getDueAt())) {
            throw ApiException.conflict("The assignment deadline has passed");
        }
        return SubmissionView.from(submissions.save(new Submission(assignment, enrollment.getStudent(),
                now, input.content())));
    }

    public List<SubmissionView> list(long id, long actorId) {
        courses.requireTeacher(assignments.courseOf(assignments.requireAssignment(id)), actorId);
        return submissions.findByAssignmentIdOrderByIdAsc(id).stream().map(SubmissionView::from).toList();
    }

    public SubmissionView submission(long id, long submissionId, long actorId) {
        Submission submission = requireSubmission(id, submissionId);
        if (submission.getStudent().getId() != actorId) {
            courses.requireTeacher(assignments.courseOf(submission.getAssignment()), actorId);
        }
        return SubmissionView.from(submission);
    }

    @Transactional
    public SubmissionView grade(long id, long submissionId, long teacherId, GradeInput input) {
        Assignment assignment = assignments.requireLockedAssignment(id);
        courses.requireTeacher(assignments.courseOf(assignment), teacherId);
        Submission submission = requireSubmission(id, submissionId);
        if (input.score() > assignment.getMaxScore()) {
            throw ApiException.invalid("score cannot exceed the assignment's maxScore");
        }
        submission.grade(input.score(), input.feedback());
        return SubmissionView.from(submission);
    }

    private Submission requireSubmission(long id, long submissionId) {
        return ApiException.require(submissions.findById(submissionId)
                .filter(submission -> submission.getAssignment().getId() == id), "Submission", submissionId);
    }
}
