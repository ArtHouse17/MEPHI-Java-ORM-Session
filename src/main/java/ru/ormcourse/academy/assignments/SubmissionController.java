package ru.ormcourse.academy.assignments;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.ormcourse.academy.assignments.AssignmentDtos.GradeInput;
import ru.ormcourse.academy.assignments.AssignmentDtos.SubmissionInput;
import ru.ormcourse.academy.assignments.AssignmentDtos.SubmissionView;
import ru.ormcourse.academy.common.Responses;

@RestController
@RequestMapping("/api/assignments/{id}/submissions")
public class SubmissionController {
    private final SubmissionService submissions;

    public SubmissionController(SubmissionService submissions) {
        this.submissions = submissions;
    }

    @PostMapping
    ResponseEntity<SubmissionView> submit(@PathVariable @Positive long id,
                                         @RequestHeader("X-Actor-Id") @Positive long studentId,
                                         @Valid @RequestBody SubmissionInput input) {
        SubmissionView created = submissions.submit(id, studentId, input);
        return Responses.created(created.id(), created);
    }

    @GetMapping
    List<SubmissionView> list(@PathVariable @Positive long id,
                                     @RequestHeader("X-Actor-Id") @Positive long actorId) {
        return submissions.list(id, actorId);
    }

    @GetMapping("/{submissionId}")
    SubmissionView submission(@PathVariable @Positive long id, @PathVariable @Positive long submissionId,
                              @RequestHeader("X-Actor-Id") @Positive long actorId) {
        return submissions.submission(id, submissionId, actorId);
    }

    @PutMapping("/{submissionId}/grade")
    SubmissionView grade(@PathVariable @Positive long id, @PathVariable @Positive long submissionId,
                         @RequestHeader("X-Actor-Id") @Positive long actorId, @Valid @RequestBody GradeInput input) {
        return submissions.grade(id, submissionId, actorId, input);
    }
}
