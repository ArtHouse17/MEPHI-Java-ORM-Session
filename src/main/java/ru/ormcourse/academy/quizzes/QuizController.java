package ru.ormcourse.academy.quizzes;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.ormcourse.academy.common.Responses;
import ru.ormcourse.academy.quizzes.QuizDtos.OptionInput;
import ru.ormcourse.academy.quizzes.QuizDtos.OptionView;
import ru.ormcourse.academy.quizzes.QuizDtos.QuestionInput;
import ru.ormcourse.academy.quizzes.QuizDtos.QuestionView;
import ru.ormcourse.academy.quizzes.QuizDtos.QuizInput;
import ru.ormcourse.academy.quizzes.QuizDtos.QuizPaper;
import ru.ormcourse.academy.quizzes.QuizDtos.QuizView;

@RestController
@RequestMapping("/api")
public class QuizController {
    private final QuizService quizzes;

    public QuizController(QuizService quizzes) {
        this.quizzes = quizzes;
    }

    @GetMapping("/quizzes")
    List<QuizView> listQuizzes(@RequestParam @Positive long moduleId) {
        return quizzes.list(moduleId);
    }

    @GetMapping("/quizzes/{id}")
    QuizPaper quiz(@PathVariable @Positive long id) {
        return quizzes.paper(id);
    }

    @PostMapping("/quizzes")
    ResponseEntity<QuizView> createQuiz(@RequestParam @Positive long moduleId,
                                        @RequestHeader("X-Actor-Id") @Positive long actorId,
                                        @Valid @RequestBody QuizInput input) {
        QuizView created = quizzes.create(moduleId, actorId, input);
        return Responses.created(created.id(), created);
    }

    @PutMapping("/quizzes/{id}")
    QuizView updateQuiz(@PathVariable @Positive long id, @RequestHeader("X-Actor-Id") @Positive long actorId,
                        @Valid @RequestBody QuizInput input) {
        return quizzes.update(id, actorId, input);
    }

    @DeleteMapping("/quizzes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteQuiz(@PathVariable @Positive long id, @RequestHeader("X-Actor-Id") @Positive long actorId) {
        quizzes.delete(id, actorId);
    }

    @GetMapping("/questions")
    List<QuestionView> questions(@RequestParam @Positive long quizId) {
        return quizzes.questions(quizId);
    }

    @GetMapping("/questions/{id}")
    QuestionView question(@PathVariable @Positive long id) {
        return quizzes.question(id);
    }

    @PostMapping("/questions")
    ResponseEntity<QuestionView> createQuestion(@RequestParam @Positive long quizId,
                                                 @RequestHeader("X-Actor-Id") @Positive long actorId,
                                                 @Valid @RequestBody QuestionInput input) {
        QuestionView created = quizzes.createQuestion(quizId, actorId, input);
        return Responses.created(created.id(), created);
    }

    @PutMapping("/questions/{id}")
    QuestionView updateQuestion(@PathVariable @Positive long id, @RequestHeader("X-Actor-Id") @Positive long actorId,
                                 @Valid @RequestBody QuestionInput input) {
        return quizzes.updateQuestion(id, actorId, input);
    }

    @DeleteMapping("/questions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteQuestion(@PathVariable @Positive long id, @RequestHeader("X-Actor-Id") @Positive long actorId) {
        quizzes.deleteQuestion(id, actorId);
    }

    @GetMapping("/answer-options")
    List<OptionView> options(@RequestParam @Positive long questionId, @RequestHeader("X-Actor-Id") @Positive long actorId) {
        return quizzes.options(questionId, actorId);
    }

    @GetMapping("/answer-options/{id}")
    OptionView option(@PathVariable @Positive long id, @RequestHeader("X-Actor-Id") @Positive long actorId) {
        return quizzes.option(id, actorId);
    }

    @PostMapping("/answer-options")
    ResponseEntity<OptionView> createOption(@RequestParam @Positive long questionId,
                                             @RequestHeader("X-Actor-Id") @Positive long actorId,
                                             @Valid @RequestBody OptionInput input) {
        OptionView created = quizzes.createOption(questionId, actorId, input);
        return Responses.created(created.id(), created);
    }

    @PutMapping("/answer-options/{id}")
    OptionView updateOption(@PathVariable @Positive long id, @RequestHeader("X-Actor-Id") @Positive long actorId,
                             @Valid @RequestBody OptionInput input) {
        return quizzes.updateOption(id, actorId, input);
    }

    @DeleteMapping("/answer-options/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteOption(@PathVariable @Positive long id, @RequestHeader("X-Actor-Id") @Positive long actorId) {
        quizzes.deleteOption(id, actorId);
    }
}
