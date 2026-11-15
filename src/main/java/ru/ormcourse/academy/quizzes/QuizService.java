package ru.ormcourse.academy.quizzes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ormcourse.academy.catalog.ContentService;
import ru.ormcourse.academy.catalog.CourseModule;
import ru.ormcourse.academy.catalog.CourseService;
import ru.ormcourse.academy.common.ApiException;
import ru.ormcourse.academy.quizzes.QuizDtos.OptionChoice;
import ru.ormcourse.academy.quizzes.QuizDtos.OptionInput;
import ru.ormcourse.academy.quizzes.QuizDtos.OptionView;
import ru.ormcourse.academy.quizzes.QuizDtos.QuestionInput;
import ru.ormcourse.academy.quizzes.QuizDtos.QuestionView;
import ru.ormcourse.academy.quizzes.QuizDtos.QuizInput;
import ru.ormcourse.academy.quizzes.QuizDtos.QuizPaper;
import ru.ormcourse.academy.quizzes.QuizDtos.QuizQuestion;
import ru.ormcourse.academy.quizzes.QuizDtos.QuizView;

@Service
@Transactional(readOnly = true)
public class QuizService {
    private final QuizRepository quizzes;
    private final QuestionRepository questions;
    private final AnswerOptionRepository options;
    private final QuizSubmissionRepository attempts;
    private final ContentService content;
    private final CourseService courses;

    public QuizService(QuizRepository quizzes, QuestionRepository questions, AnswerOptionRepository options,
                       QuizSubmissionRepository attempts, ContentService content, CourseService courses) {
        this.quizzes = quizzes;
        this.questions = questions;
        this.options = options;
        this.attempts = attempts;
        this.content = content;
        this.courses = courses;
    }

    public List<QuizView> list(long moduleId) {
        content.requireModule(moduleId);
        return quizzes.findByModuleId(moduleId).stream().map(QuizView::from).toList();
    }

    public QuizPaper paper(long quizId) {
        Quiz quiz = requireQuiz(quizId);
        Map<Long, List<AnswerOption>> byQuestion = options.findByQuestionQuizIdOrderByPositionAscIdAsc(quizId)
                .stream().collect(Collectors.groupingBy(option -> option.getQuestion().getId()));
        List<QuizQuestion> paper = questions.findByQuizIdOrderByPositionAscIdAsc(quizId).stream()
                .map(question -> new QuizQuestion(QuestionView.from(question),
                        byQuestion.getOrDefault(question.getId(), List.of()).stream()
                                .map(OptionChoice::from).toList()))
                .toList();
        return new QuizPaper(QuizView.from(quiz), paper);
    }

    @Transactional
    public QuizView create(long moduleId, long teacherId, QuizInput input) {
        CourseModule module = content.requireModule(moduleId);
        courses.requireTeacher(module.getCourse(), teacherId);
        if (quizzes.findByModuleId(moduleId).isPresent()) {
            throw ApiException.conflict("The module already has a quiz");
        }
        return QuizView.from(quizzes.save(new Quiz(module, input.title())));
    }

    @Transactional
    public QuizView update(long id, long teacherId, QuizInput input) {
        Quiz quiz = requireEditableQuiz(id, teacherId);
        quiz.update(input.title());
        return QuizView.from(quiz);
    }

    @Transactional
    public void delete(long id, long teacherId) {
        quizzes.delete(requireEditableQuiz(id, teacherId));
    }

    public List<QuestionView> questions(long quizId) {
        requireQuiz(quizId);
        return questions.findByQuizIdOrderByPositionAscIdAsc(quizId).stream().map(QuestionView::from).toList();
    }

    public QuestionView question(long id) {
        return QuestionView.from(requireQuestion(id));
    }

    @Transactional
    public QuestionView createQuestion(long quizId, long teacherId, QuestionInput input) {
        Quiz quiz = requireEditableQuiz(quizId, teacherId);
        if (questions.countByQuizId(quizId) >= QuizDtos.MAX_QUESTIONS) {
            throw ApiException.conflict("The quiz has reached its question limit");
        }
        return QuestionView.from(questions.save(new Question(quiz, input.text(), input.points(), input.position())));
    }

    @Transactional
    public QuestionView updateQuestion(long id, long teacherId, QuestionInput input) {
        Question question = requireEditableQuestion(id, teacherId);
        question.update(input.text(), input.points(), input.position());
        return QuestionView.from(question);
    }

    @Transactional
    public void deleteQuestion(long id, long teacherId) {
        questions.delete(requireEditableQuestion(id, teacherId));
    }

    public List<OptionView> options(long questionId, long teacherId) {
        Question question = requireQuestion(questionId);
        courses.requireTeacher(question.getQuiz().getModule().getCourse(), teacherId);
        return options.findByQuestionIdOrderByPositionAscIdAsc(questionId).stream().map(OptionView::from).toList();
    }

    public OptionView option(long id, long teacherId) {
        AnswerOption option = requireOption(id);
        courses.requireTeacher(option.getQuestion().getQuiz().getModule().getCourse(), teacherId);
        return OptionView.from(option);
    }

    @Transactional
    public OptionView createOption(long questionId, long teacherId, OptionInput input) {
        Question question = requireEditableQuestion(questionId, teacherId);
        return OptionView.from(options.save(new AnswerOption(question, input.text(), input.correct(), input.position())));
    }

    @Transactional
    public OptionView updateOption(long id, long teacherId, OptionInput input) {
        AnswerOption option = requireEditableOption(id, teacherId);
        option.update(input.text(), input.correct(), input.position());
        return OptionView.from(option);
    }

    @Transactional
    public void deleteOption(long id, long teacherId) {
        options.delete(requireEditableOption(id, teacherId));
    }

    Quiz requireQuiz(long id) {
        return ApiException.require(quizzes.findById(id), "Quiz", id);
    }

    Quiz requireLockedQuiz(long id) {
        return ApiException.require(quizzes.findForUpdate(id), "Quiz", id);
    }

    private Quiz requireEditableQuiz(long id, long teacherId) {
        Quiz quiz = requireLockedQuiz(id);
        courses.requireTeacher(quiz.getModule().getCourse(), teacherId);
        if (attempts.existsByQuizId(id)) {
            throw ApiException.conflict("The quiz is locked because students have submitted results");
        }
        return quiz;
    }

    private Question requireQuestion(long id) {
        return ApiException.require(questions.findById(id), "Question", id);
    }

    private Question requireEditableQuestion(long id, long teacherId) {
        Question question = requireQuestion(id);
        requireEditableQuiz(question.getQuiz().getId(), teacherId);
        return question;
    }

    private AnswerOption requireOption(long id) {
        return ApiException.require(options.findById(id), "Answer option", id);
    }

    private AnswerOption requireEditableOption(long id, long teacherId) {
        AnswerOption option = requireOption(id);
        requireEditableQuestion(option.getQuestion().getId(), teacherId);
        return option;
    }
}
