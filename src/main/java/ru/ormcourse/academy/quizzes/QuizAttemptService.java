package ru.ormcourse.academy.quizzes;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ormcourse.academy.catalog.Course;
import ru.ormcourse.academy.catalog.CourseActivityService;
import ru.ormcourse.academy.catalog.CourseService;
import ru.ormcourse.academy.catalog.Enrollment;
import ru.ormcourse.academy.common.ApiException;
import ru.ormcourse.academy.quizzes.QuizDtos.AnswerView;
import ru.ormcourse.academy.quizzes.QuizDtos.AttemptInput;
import ru.ormcourse.academy.quizzes.QuizDtos.AttemptSummary;
import ru.ormcourse.academy.quizzes.QuizDtos.AttemptView;
import ru.ormcourse.academy.quizzes.QuizDtos.PickInput;
import ru.ormcourse.academy.quizzes.QuizScorer.Grade;
import ru.ormcourse.academy.quizzes.QuizScorer.QuestionKey;
import ru.ormcourse.academy.quizzes.QuizScorer.Selection;

@Service
@Transactional(readOnly = true)
public class QuizAttemptService {
    private final QuizService quizzes;
    private final CourseService courses;
    private final CourseActivityService activity;
    private final QuestionRepository questions;
    private final AnswerOptionRepository options;
    private final QuizSubmissionRepository attempts;
    private final QuizAnswerRepository answers;
    private final Clock clock;

    public QuizAttemptService(QuizService quizzes, CourseService courses, CourseActivityService activity,
                              QuestionRepository questions, AnswerOptionRepository options,
                              QuizSubmissionRepository attempts, QuizAnswerRepository answers, Clock clock) {
        this.quizzes = quizzes;
        this.courses = courses;
        this.activity = activity;
        this.questions = questions;
        this.options = options;
        this.attempts = attempts;
        this.answers = answers;
        this.clock = clock;
    }

    @Transactional
    public AttemptView take(long quizId, long studentId, AttemptInput input) {
        Quiz quiz = quizzes.requireLockedQuiz(quizId);
        Enrollment enrollment = activity.requireEnrollment(courseOf(quiz).getId(), studentId);
        if (attempts.existsByQuizIdAndStudentId(quizId, studentId)) {
            throw ApiException.conflict("The student already submitted this quiz");
        }

        List<AnswerOption> quizOptions = options.findByQuestionQuizIdOrderByPositionAscIdAsc(quizId);
        List<QuestionKey> key = buildKey(quizId, quizOptions);
        Grade grade = grade(key, input.answers());
        QuizSubmission attempt = attempts.save(new QuizSubmission(quiz, enrollment.getStudent(), clock.instant(),
                grade.score(), grade.maxScore()));
        List<QuizAnswer> selected = saveAnswers(attempt, input.answers(), quizOptions, grade);
        return view(attempt, selected);
    }

    private List<QuestionKey> buildKey(long quizId, List<AnswerOption> quizOptions) {
        List<Question> quizQuestions = questions.findByQuizIdOrderByPositionAscIdAsc(quizId);
        if (quizQuestions.isEmpty()) {
            throw ApiException.conflict("The quiz is not ready: it has no questions");
        }
        Map<Long, List<AnswerOption>> optionsByQuestion = quizOptions.stream()
                .collect(Collectors.groupingBy(option -> option.getQuestion().getId()));
        return quizQuestions.stream()
                .map(question -> answerKey(question, optionsByQuestion.getOrDefault(question.getId(), List.of())))
                .toList();
    }

    private List<QuizAnswer> saveAnswers(QuizSubmission attempt, List<PickInput> picks,
                                        List<AnswerOption> quizOptions, Grade grade) {
        Map<Long, AnswerOption> optionsById = quizOptions.stream()
                .collect(Collectors.toMap(AnswerOption::getId, Function.identity()));
        List<QuizAnswer> selected = new ArrayList<>();
        for (PickInput pick : picks) {
            AnswerOption option = optionsById.get(pick.optionId());
            int points = grade.pointsByQuestion().get(pick.questionId());
            selected.add(new QuizAnswer(attempt, option.getQuestion(), option, points));
        }
        return answers.saveAll(selected);
    }

    public List<AttemptSummary> list(long quizId, long teacherId) {
        Quiz quiz = quizzes.requireQuiz(quizId);
        courses.requireTeacher(courseOf(quiz), teacherId);
        return attempts.findByQuizIdOrderByIdAsc(quizId).stream().map(AttemptSummary::from).toList();
    }

    public AttemptView get(long quizId, long attemptId, long actorId) {
        QuizSubmission attempt = ApiException.require(attempts.findById(attemptId)
                .filter(existing -> existing.getQuiz().getId() == quizId), "Quiz result", attemptId);
        if (attempt.getStudent().getId() != actorId) {
            courses.requireTeacher(courseOf(attempt.getQuiz()), actorId);
        }
        return view(attempt, answers.findBySubmissionIdOrderByIdAsc(attemptId));
    }

    public AttemptView ownResult(long quizId, long studentId) {
        quizzes.requireQuiz(quizId);
        QuizSubmission attempt = attempts.findByQuizIdAndStudentId(quizId, studentId)
                .orElseThrow(() -> ApiException.notFound("Quiz result for student", studentId));
        return view(attempt, answers.findBySubmissionIdOrderByIdAsc(attempt.getId()));
    }

    private AttemptView view(QuizSubmission attempt, List<QuizAnswer> selected) {
        return new AttemptView(AttemptSummary.from(attempt), selected.stream().map(AnswerView::from).toList());
    }

    private QuestionKey answerKey(Question question, List<AnswerOption> choices) {
        List<AnswerOption> correct = choices.stream().filter(AnswerOption::getCorrect).toList();
        if (choices.size() < 2 || correct.size() != 1) {
            throw ApiException.conflict("Question #" + question.getId()
                    + " needs at least two options and exactly one correct option");
        }
        Set<Long> optionIds = choices.stream().map(AnswerOption::getId).collect(Collectors.toSet());
        return new QuestionKey(question.getId(), question.getPoints(), optionIds, correct.getFirst().getId());
    }

    private Grade grade(List<QuestionKey> key, List<PickInput> picks) {
        List<Selection> selections = picks.stream()
                .map(pick -> new Selection(pick.questionId(), pick.optionId())).toList();
        try {
            return QuizScorer.grade(key, selections);
        } catch (IllegalArgumentException exception) {
            throw ApiException.invalid(exception.getMessage());
        }
    }

    private Course courseOf(Quiz quiz) {
        return quiz.getModule().getCourse();
    }
}
