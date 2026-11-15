package ru.ormcourse.academy.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.ormcourse.academy.dto.AssignmentDtos.AssignmentInput;
import ru.ormcourse.academy.dto.AssignmentDtos.AssignmentView;
import ru.ormcourse.academy.service.AssignmentService;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    private final AssignmentService assignments;

    public AssignmentController(AssignmentService assignments) {
        this.assignments = assignments;
    }

    @GetMapping
    List<AssignmentView> list(@RequestParam @Positive long lessonId) {
        return assignments.list(lessonId);
    }

    @GetMapping("/{id}")
    AssignmentView get(@PathVariable @Positive long id) {
        return assignments.get(id);
    }

    @PostMapping
    ResponseEntity<AssignmentView> create(@RequestParam @Positive long lessonId,
                                           @RequestHeader("X-Actor-Id") @Positive long actorId,
                                           @Valid @RequestBody AssignmentInput input) {
        AssignmentView created = assignments.create(lessonId, actorId, input);
        return Responses.created(created.id(), created);
    }

    @PutMapping("/{id}")
    AssignmentView update(@PathVariable @Positive long id,
                          @RequestHeader("X-Actor-Id") @Positive long actorId,
                          @Valid @RequestBody AssignmentInput input) {
        return assignments.update(id, actorId, input);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable @Positive long id, @RequestHeader("X-Actor-Id") @Positive long actorId) {
        assignments.delete(id, actorId);
    }
}
