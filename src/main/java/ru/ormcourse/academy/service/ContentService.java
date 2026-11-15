package ru.ormcourse.academy.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ormcourse.academy.dto.CatalogDtos.LessonInput;
import ru.ormcourse.academy.dto.CatalogDtos.LessonView;
import ru.ormcourse.academy.dto.CatalogDtos.ModuleInput;
import ru.ormcourse.academy.dto.CatalogDtos.ModuleView;
import ru.ormcourse.academy.dto.CatalogDtos.ResourceInput;
import ru.ormcourse.academy.dto.CatalogDtos.ResourceView;
import ru.ormcourse.academy.dto.CatalogDtos.SessionInput;
import ru.ormcourse.academy.dto.CatalogDtos.SessionView;
import ru.ormcourse.academy.entity.Course;
import ru.ormcourse.academy.entity.CourseModule;
import ru.ormcourse.academy.entity.Lesson;
import ru.ormcourse.academy.entity.LessonResource;
import ru.ormcourse.academy.entity.LessonSession;
import ru.ormcourse.academy.exception.ApiException;
import ru.ormcourse.academy.repository.CourseModuleRepository;
import ru.ormcourse.academy.repository.LessonRepository;
import ru.ormcourse.academy.repository.LessonResourceRepository;
import ru.ormcourse.academy.repository.LessonSessionRepository;

@Service
@Transactional(readOnly = true)
public class ContentService {
    private final CourseService courses;
    private final CourseModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final LessonResourceRepository resourceRepository;
    private final LessonSessionRepository sessionRepository;

    public ContentService(CourseService courses,
                          CourseModuleRepository moduleRepository,
                          LessonRepository lessonRepository,
                          LessonResourceRepository resourceRepository,
                          LessonSessionRepository sessionRepository) {
        this.courses = courses;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.resourceRepository = resourceRepository;
        this.sessionRepository = sessionRepository;
    }

    public List<ModuleView> listModules(long courseId) {
        courses.requireCourse(courseId);
        return moduleRepository.findByCourseIdOrderByPositionAscIdAsc(courseId)
                .stream().map(ModuleView::from).toList();
    }

    public ModuleView getModule(long id) {
        return ModuleView.from(requireModule(id));
    }

    @Transactional
    public ModuleView createModule(long courseId, ModuleInput input) {
        Course parent = courses.requireCourse(courseId);
        CourseModule module = new CourseModule(parent, input.title(), input.description(), input.position());
        return ModuleView.from(moduleRepository.save(module));
    }

    @Transactional
    public ModuleView updateModule(long id, ModuleInput input) {
        CourseModule module = requireModule(id);
        module.update(input.title(), input.description(), input.position());
        return ModuleView.from(module);
    }

    @Transactional
    public void deleteModule(long id) {
        moduleRepository.delete(requireModule(id));
    }

    public CourseModule requireModule(long id) {
        return ApiException.require(moduleRepository.findById(id), "Module", id);
    }

    public List<LessonView> listLessons(long moduleId) {
        requireModule(moduleId);
        return lessonRepository.findByModuleIdOrderByPositionAscIdAsc(moduleId)
                .stream().map(LessonView::from).toList();
    }

    public LessonView getLesson(long id) {
        return LessonView.from(requireLesson(id));
    }

    @Transactional
    public LessonView createLesson(long moduleId, LessonInput input) {
        CourseModule parent = requireModule(moduleId);
        Lesson lesson = new Lesson(parent, input.title(), input.content(), input.position());
        return LessonView.from(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonView updateLesson(long id, LessonInput input) {
        Lesson lesson = requireLesson(id);
        lesson.update(input.title(), input.content(), input.position());
        return LessonView.from(lesson);
    }

    @Transactional
    public void deleteLesson(long id) {
        lessonRepository.delete(requireLesson(id));
    }

    public Lesson requireLesson(long id) {
        return ApiException.require(lessonRepository.findById(id), "Lesson", id);
    }

    public List<ResourceView> listResources(long lessonId) {
        requireLesson(lessonId);
        return resourceRepository.findByLessonIdOrderByIdAsc(lessonId)
                .stream().map(ResourceView::from).toList();
    }

    public ResourceView getResource(long id) {
        return ResourceView.from(requireResource(id));
    }

    @Transactional
    public ResourceView createResource(long lessonId, ResourceInput input) {
        Lesson parent = requireLesson(lessonId);
        LessonResource resource = new LessonResource(parent, input.title(), input.url());
        return ResourceView.from(resourceRepository.save(resource));
    }

    @Transactional
    public ResourceView updateResource(long id, ResourceInput input) {
        LessonResource resource = requireResource(id);
        resource.update(input.title(), input.url());
        return ResourceView.from(resource);
    }

    @Transactional
    public void deleteResource(long id) {
        resourceRepository.delete(requireResource(id));
    }

    private LessonResource requireResource(long id) {
        return ApiException.require(resourceRepository.findById(id), "Lesson resource", id);
    }

    public List<SessionView> listSessions(long lessonId) {
        requireLesson(lessonId);
        return sessionRepository.findByLessonIdOrderByStartsAtAscIdAsc(lessonId)
                .stream().map(SessionView::from).toList();
    }

    public SessionView getSession(long id) {
        return SessionView.from(requireSession(id));
    }

    @Transactional
    public SessionView createSession(long lessonId, SessionInput input) {
        validateSession(input);
        Lesson parent = requireLesson(lessonId);
        LessonSession session = new LessonSession(parent, input.startsAt(), input.endsAt(), input.location());
        return SessionView.from(sessionRepository.save(session));
    }

    @Transactional
    public SessionView updateSession(long id, SessionInput input) {
        validateSession(input);
        LessonSession session = requireSession(id);
        session.update(input.startsAt(), input.endsAt(), input.location());
        return SessionView.from(session);
    }

    @Transactional
    public void deleteSession(long id) {
        sessionRepository.delete(requireSession(id));
    }

    private LessonSession requireSession(long id) {
        return ApiException.require(sessionRepository.findById(id), "Lesson session", id);
    }

    public List<SessionView> schedule(long courseId) {
        courses.requireCourse(courseId);
        return sessionRepository.findByLessonModuleCourseIdOrderByStartsAtAscIdAsc(courseId)
                .stream().map(SessionView::from).toList();
    }

    private void validateSession(SessionInput input) {
        if (!input.endsAt().isAfter(input.startsAt())) {
            throw ApiException.invalid("endsAt must be later than startsAt");
        }
    }
}
