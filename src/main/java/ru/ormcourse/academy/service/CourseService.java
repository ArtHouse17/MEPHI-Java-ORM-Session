package ru.ormcourse.academy.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ormcourse.academy.dto.CatalogDtos.CourseInput;
import ru.ormcourse.academy.dto.CatalogDtos.CourseView;
import ru.ormcourse.academy.dto.CatalogDtos.Curriculum;
import ru.ormcourse.academy.dto.CatalogDtos.LessonView;
import ru.ormcourse.academy.dto.CatalogDtos.ModuleContent;
import ru.ormcourse.academy.dto.CatalogDtos.ModuleView;
import ru.ormcourse.academy.dto.CatalogDtos.TagView;
import ru.ormcourse.academy.entity.Category;
import ru.ormcourse.academy.entity.Course;
import ru.ormcourse.academy.entity.CourseModule;
import ru.ormcourse.academy.entity.Lesson;
import ru.ormcourse.academy.entity.Role;
import ru.ormcourse.academy.entity.Tag;
import ru.ormcourse.academy.entity.UserAccount;
import ru.ormcourse.academy.exception.ApiException;
import ru.ormcourse.academy.repository.CategoryRepository;
import ru.ormcourse.academy.repository.CourseRepository;
import ru.ormcourse.academy.repository.LessonRepository;
import ru.ormcourse.academy.repository.TagRepository;

@Service
@Transactional(readOnly = true)
public class CourseService {
    private final CourseRepository courses;
    private final CategoryRepository categories;
    private final TagRepository tags;
    private final LessonRepository lessons;
    private final AccountService accounts;

    public CourseService(CourseRepository courses, CategoryRepository categories,
                         TagRepository tags, LessonRepository lessons, AccountService accounts) {
        this.courses = courses;
        this.categories = categories;
        this.tags = tags;
        this.lessons = lessons;
        this.accounts = accounts;
    }

    public List<CourseView> list(Long tagId) {
        List<Course> found = tagId == null
                ? courses.findAllByOrderByIdAsc() : courses.findDistinctByTagsIdOrderByIdAsc(tagId);
        return found.stream().map(CourseView::from).toList();
    }

    public CourseView get(long id) {
        return CourseView.from(requireCourse(id));
    }

    @Transactional
    public CourseView create(CourseInput input) {
        Category category = requireCategory(input.categoryId());
        UserAccount teacher = accounts.requireRole(input.teacherId(), Role.TEACHER);
        Course course = new Course(input.title(), input.description(), category, teacher,
                input.startDate(), input.durationWeeks());
        return CourseView.from(courses.save(course));
    }

    @Transactional
    public CourseView update(long id, CourseInput input) {
        Course course = requireCourse(id);
        Category category = requireCategory(input.categoryId());
        UserAccount teacher = accounts.requireRole(input.teacherId(), Role.TEACHER);
        course.update(input.title(), input.description(), category, teacher, input.startDate(), input.durationWeeks());
        return CourseView.from(course);
    }

    @Transactional
    public void delete(long id) {
        courses.delete(requireCourse(id));
    }

    public Curriculum curriculum(long id) {
        Course course = ApiException.require(courses.findCurriculumById(id), "Course", id);
        Map<Long, List<LessonView>> lessonsByModule = new HashMap<>();
        for (Lesson lesson : lessons.findByModuleCourseIdOrderByPositionAscIdAsc(id)) {
            long moduleId = lesson.getModule().getId();
            lessonsByModule.computeIfAbsent(moduleId, key -> new ArrayList<>()).add(LessonView.from(lesson));
        }
        List<ModuleContent> modules = new ArrayList<>();
        for (CourseModule module : course.getModules()) {
            List<LessonView> moduleLessons = lessonsByModule.getOrDefault(module.getId(), List.of());
            modules.add(new ModuleContent(ModuleView.from(module), moduleLessons));
        }
        return new Curriculum(CourseView.from(course), modules);
    }

    public List<TagView> tags(long courseId) {
        return tagViews(courseWithTags(courseId));
    }

    @Transactional
    public List<TagView> replaceTags(long courseId, Set<Long> tagIds) {
        Course course = courseWithTags(courseId);
        List<Tag> found = tags.findAllById(tagIds);
        if (found.size() != tagIds.size()) {
            throw ApiException.invalid("One or more tag IDs do not exist");
        }
        course.replaceTags(found);
        return tagViews(course);
    }

    public Course requireCourse(long id) {
        return ApiException.require(courses.findById(id), "Course", id);
    }

    public void requireTeacher(Course course, long actorId) {
        if (course.getTeacher().getId() != actorId) {
            throw ApiException.forbidden("Only the course teacher can perform this operation");
        }
    }

    private Course courseWithTags(long id) {
        return ApiException.require(courses.findWithTagsById(id), "Course", id);
    }

    private Category requireCategory(long id) {
        return ApiException.require(categories.findById(id), "Category", id);
    }

    private List<TagView> tagViews(Course course) {
        return course.getTags().stream().map(TagView::from)
                .sorted(Comparator.comparingLong(TagView::id)).toList();
    }
}
