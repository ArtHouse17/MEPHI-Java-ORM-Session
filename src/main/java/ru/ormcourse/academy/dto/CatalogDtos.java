package ru.ormcourse.academy.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import ru.ormcourse.academy.entity.Category;
import ru.ormcourse.academy.entity.Course;
import ru.ormcourse.academy.entity.CourseModule;
import ru.ormcourse.academy.entity.Lesson;
import ru.ormcourse.academy.entity.LessonResource;
import ru.ormcourse.academy.entity.LessonSession;
import ru.ormcourse.academy.entity.Tag;

public final class CatalogDtos {
    private CatalogDtos() {
    }

    public record CategoryInput(@NotBlank @Size(max = 100) String name) {
    }

    public record TagInput(@NotBlank @Size(max = 80) String name) {
    }

    public record TagsInput(@NotNull @Size(max = 50) Set<@NotNull @Positive Long> tagIds) {
    }

    public record CourseInput(
            @NotBlank @Size(max = 180) String title,
            @NotBlank @Size(max = 4000) String description,
            @NotNull @Positive Long categoryId,
            @NotNull @Positive Long teacherId,
            @NotNull LocalDate startDate,
            @NotNull @Min(1) @Max(104) Integer durationWeeks) {
    }

    public record ModuleInput(
            @NotBlank @Size(max = 180) String title,
            @NotBlank @Size(max = 4000) String description,
            @NotNull @Positive Integer position) {
    }

    public record LessonInput(
            @NotBlank @Size(max = 180) String title,
            @NotBlank @Size(max = 12000) String content,
            @NotNull @Positive Integer position) {
    }

    public record ResourceInput(
            @NotBlank @Size(max = 180) String title,
            @NotBlank @Size(max = 1000) @Pattern(regexp = "https?://[^\\s/]+[^\\s]*") String url) {
    }

    public record SessionInput(
            @NotNull Instant startsAt,
            @NotNull Instant endsAt,
            @NotBlank @Size(max = 500) String location) {
    }

    public record CategoryView(long id, String name) {
        public static CategoryView from(Category category) {
            return new CategoryView(category.getId(), category.getName());
        }
    }

    public record TagView(long id, String name) {
        public static TagView from(Tag tag) {
            return new TagView(tag.getId(), tag.getName());
        }
    }

    public record CourseView(long id, String title, String description, long categoryId, String category,
                             long teacherId, String teacher, LocalDate startDate, int durationWeeks) {
        public static CourseView from(Course course) {
            return new CourseView(course.getId(), course.getTitle(), course.getDescription(),
                    course.getCategory().getId(), course.getCategory().getName(),
                    course.getTeacher().getId(), course.getTeacher().getName(),
                    course.getStartDate(), course.getDurationWeeks());
        }
    }

    public record ModuleView(long id, long courseId, String title, String description, int position) {
        public static ModuleView from(CourseModule module) {
            return new ModuleView(module.getId(), module.getCourse().getId(), module.getTitle(),
                    module.getDescription(), module.getPosition());
        }
    }

    public record LessonView(long id, long moduleId, String title, String content, int position) {
        public static LessonView from(Lesson lesson) {
            return new LessonView(lesson.getId(), lesson.getModule().getId(), lesson.getTitle(),
                    lesson.getContent(), lesson.getPosition());
        }
    }

    public record ResourceView(long id, long lessonId, String title, String url) {
        public static ResourceView from(LessonResource resource) {
            return new ResourceView(resource.getId(), resource.getLesson().getId(),
                    resource.getTitle(), resource.getUrl());
        }
    }

    public record SessionView(long id, long lessonId, String lessonTitle,
                              Instant startsAt, Instant endsAt, String location) {
        public static SessionView from(LessonSession session) {
            return new SessionView(session.getId(), session.getLesson().getId(), session.getLesson().getTitle(),
                    session.getStartsAt(), session.getEndsAt(), session.getLocation());
        }
    }

    public record Curriculum(CourseView course, List<ModuleContent> modules) {
    }

    public record ModuleContent(ModuleView module, List<LessonView> lessons) {
    }
}
