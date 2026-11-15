package ru.ormcourse.academy.assignments;

import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import ru.ormcourse.academy.assignments.AssignmentDtos.AssignmentInput;
import ru.ormcourse.academy.assignments.AssignmentDtos.AssignmentView;
import ru.ormcourse.academy.catalog.ContentService;
import ru.ormcourse.academy.catalog.Course;
import ru.ormcourse.academy.catalog.CourseService;
import ru.ormcourse.academy.catalog.Lesson;
import ru.ormcourse.academy.common.ApiException;

@Service
@Transactional(readOnly = true)
public class AssignmentService {
    private final AssignmentRepository assignments;
    private final SubmissionRepository submissions;
    private final ContentService content;
    private final CourseService courses;

    public AssignmentService(AssignmentRepository assignments, SubmissionRepository submissions,
                             ContentService content, CourseService courses) {
        this.assignments = assignments;
        this.submissions = submissions;
        this.content = content;
        this.courses = courses;
    }

    public List<AssignmentView> list(long lessonId) {
        content.requireLesson(lessonId);
        return assignments.findByLessonIdOrderByIdAsc(lessonId).stream().map(AssignmentView::from).toList();
    }

    public AssignmentView get(long id) {
        return AssignmentView.from(requireAssignment(id));
    }

    @Transactional
    public AssignmentView create(long lessonId, long actorId, AssignmentInput input) {
        Lesson lesson = content.requireLesson(lessonId);
        courses.requireTeacher(lesson.getModule().getCourse(), actorId);
        Assignment assignment = new Assignment(lesson, input.title(), input.description(), input.dueAt(),
                input.maxScore());
        return AssignmentView.from(assignments.save(assignment));
    }

    @Transactional
    public AssignmentView update(long id, long actorId, AssignmentInput input) {
        Assignment assignment = requireLockedAssignment(id);
        courses.requireTeacher(courseOf(assignment), actorId);
        if (submissions.existsByAssignmentIdAndScoreGreaterThan(id, input.maxScore())) {
            throw ApiException.conflict("maxScore cannot be lower than an existing grade");
        }
        assignment.update(input.title(), input.description(), input.dueAt(), input.maxScore());
        return AssignmentView.from(assignment);
    }

    @Transactional
    public void delete(long id, long actorId) {
        Assignment assignment = requireLockedAssignment(id);
        courses.requireTeacher(courseOf(assignment), actorId);
        assignments.delete(assignment);
    }

    Assignment requireAssignment(long id) {
        return ApiException.require(assignments.findById(id), "Assignment", id);
    }

    Assignment requireLockedAssignment(long id) {
        return ApiException.require(assignments.findForUpdate(id), "Assignment", id);
    }

    Course courseOf(Assignment assignment) {
        return assignment.getLesson().getModule().getCourse();
    }
}
