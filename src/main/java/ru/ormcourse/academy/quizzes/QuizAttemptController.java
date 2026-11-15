package ru.ormcourse.academy.quizzes;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.ormcourse.academy.common.Responses;
import ru.ormcourse.academy.quizzes.QuizDtos.AttemptInput;
import ru.ormcourse.academy.quizzes.QuizDtos.AttemptSummary;
import ru.ormcourse.academy.quizzes.QuizDtos.AttemptView;

@RestController
@RequestMapping("/api/quizzes/{id}")
public class QuizAttemptController {
    private final QuizAttemptService attempts;

    public QuizAttemptController(QuizAttemptService attempts) {
        this.attempts = attempts;
    }

    @PostMapping("/attempts")
    ResponseEntity<AttemptView> take(@PathVariable @Positive long id, @RequestHeader("X-Actor-Id") @Positive long actorId,
                                      @Valid @RequestBody AttemptInput input) {
        AttemptView created = attempts.take(id, actorId, input);
        return Responses.created(created.attempt().id(), created);
    }

    @GetMapping("/attempts")
    List<AttemptSummary> attempts(@PathVariable @Positive long id, @RequestHeader("X-Actor-Id") @Positive long actorId) {
        return attempts.list(id, actorId);
    }

    @GetMapping("/attempts/{attemptId}")
    AttemptView attempt(@PathVariable @Positive long id, @PathVariable @Positive long attemptId,
                        @RequestHeader("X-Actor-Id") @Positive long actorId) {
        return attempts.get(id, attemptId, actorId);
    }

    @GetMapping("/result")
    AttemptView result(@PathVariable @Positive long id, @RequestHeader("X-Actor-Id") @Positive long actorId) {
        return attempts.ownResult(id, actorId);
    }
}
