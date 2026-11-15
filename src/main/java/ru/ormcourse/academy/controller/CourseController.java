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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.ormcourse.academy.dto.CatalogDtos.CourseInput;
import ru.ormcourse.academy.dto.CatalogDtos.CourseView;
import ru.ormcourse.academy.dto.CatalogDtos.Curriculum;
import ru.ormcourse.academy.dto.CatalogDtos.SessionView;
import ru.ormcourse.academy.dto.CatalogDtos.TagView;
import ru.ormcourse.academy.dto.CatalogDtos.TagsInput;
import ru.ormcourse.academy.service.ContentService;
import ru.ormcourse.academy.service.CourseService;

@RestController
@RequestMapping("/api")
public class CourseController {
    private final CourseService courses;
    private final ContentService content;

    public CourseController(CourseService courses, ContentService content) {
        this.courses = courses;
        this.content = content;
    }

    @GetMapping("/courses")
    List<CourseView> listCourses(@RequestParam(required = false) @Positive Long tagId) {
        return courses.list(tagId);
    }

    @PostMapping("/courses")
    ResponseEntity<CourseView> createCourse(@Valid @RequestBody CourseInput input) {
        CourseView created = courses.create(input);
        return Responses.created(created.id(), created);
    }

    @GetMapping("/courses/{id}")
    CourseView getCourse(@PathVariable @Positive long id) {
        return courses.get(id);
    }

    @PutMapping("/courses/{id}")
    CourseView updateCourse(@PathVariable @Positive long id, @Valid @RequestBody CourseInput input) {
        return courses.update(id, input);
    }

    @DeleteMapping("/courses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCourse(@PathVariable @Positive long id) {
        courses.delete(id);
    }

    @GetMapping("/courses/{id}/curriculum")
    Curriculum curriculum(@PathVariable @Positive long id) {
        return courses.curriculum(id);
    }

    @GetMapping("/courses/{id}/schedule")
    List<SessionView> schedule(@PathVariable @Positive long id) {
        return content.schedule(id);
    }

    @GetMapping("/courses/{id}/tags")
    List<TagView> courseTags(@PathVariable @Positive long id) {
        return courses.tags(id);
    }

    @PutMapping("/courses/{id}/tags")
    List<TagView> replaceCourseTags(@PathVariable @Positive long id, @Valid @RequestBody TagsInput input) {
        return courses.replaceTags(id, input.tagIds());
    }
}
