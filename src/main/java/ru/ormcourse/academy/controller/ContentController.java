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
import ru.ormcourse.academy.dto.CatalogDtos.LessonInput;
import ru.ormcourse.academy.dto.CatalogDtos.LessonView;
import ru.ormcourse.academy.dto.CatalogDtos.ModuleInput;
import ru.ormcourse.academy.dto.CatalogDtos.ModuleView;
import ru.ormcourse.academy.dto.CatalogDtos.ResourceInput;
import ru.ormcourse.academy.dto.CatalogDtos.ResourceView;
import ru.ormcourse.academy.dto.CatalogDtos.SessionInput;
import ru.ormcourse.academy.dto.CatalogDtos.SessionView;
import ru.ormcourse.academy.service.ContentService;

@RestController
@RequestMapping("/api")
public class ContentController {
    private final ContentService content;

    public ContentController(ContentService content) {
        this.content = content;
    }

    @GetMapping("/modules")
    List<ModuleView> listModules(@RequestParam @Positive long courseId) {
        return content.listModules(courseId);
    }

    @GetMapping("/modules/{id}")
    ModuleView getModule(@PathVariable @Positive long id) {
        return content.getModule(id);
    }

    @PostMapping("/modules")
    ResponseEntity<ModuleView> createModule(@RequestParam @Positive long courseId,
                                        @Valid @RequestBody ModuleInput input) {
        ModuleView created = content.createModule(courseId, input);
        return Responses.created(created.id(), created);
    }

    @PutMapping("/modules/{id}")
    ModuleView updateModule(@PathVariable @Positive long id, @Valid @RequestBody ModuleInput input) {
        return content.updateModule(id, input);
    }

    @DeleteMapping("/modules/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteModule(@PathVariable @Positive long id) {
        content.deleteModule(id);
    }

    @GetMapping("/lessons")
    List<LessonView> listLessons(@RequestParam @Positive long moduleId) {
        return content.listLessons(moduleId);
    }

    @GetMapping("/lessons/{id}")
    LessonView getLesson(@PathVariable @Positive long id) {
        return content.getLesson(id);
    }

    @PostMapping("/lessons")
    ResponseEntity<LessonView> createLesson(@RequestParam @Positive long moduleId,
                                        @Valid @RequestBody LessonInput input) {
        LessonView created = content.createLesson(moduleId, input);
        return Responses.created(created.id(), created);
    }

    @PutMapping("/lessons/{id}")
    LessonView updateLesson(@PathVariable @Positive long id, @Valid @RequestBody LessonInput input) {
        return content.updateLesson(id, input);
    }

    @DeleteMapping("/lessons/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteLesson(@PathVariable @Positive long id) {
        content.deleteLesson(id);
    }

    @GetMapping("/lesson-resources")
    List<ResourceView> listResources(@RequestParam @Positive long lessonId) {
        return content.listResources(lessonId);
    }

    @GetMapping("/lesson-resources/{id}")
    ResourceView getResource(@PathVariable @Positive long id) {
        return content.getResource(id);
    }

    @PostMapping("/lesson-resources")
    ResponseEntity<ResourceView> createResource(@RequestParam @Positive long lessonId,
                                        @Valid @RequestBody ResourceInput input) {
        ResourceView created = content.createResource(lessonId, input);
        return Responses.created(created.id(), created);
    }

    @PutMapping("/lesson-resources/{id}")
    ResourceView updateResource(@PathVariable @Positive long id, @Valid @RequestBody ResourceInput input) {
        return content.updateResource(id, input);
    }

    @DeleteMapping("/lesson-resources/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteResource(@PathVariable @Positive long id) {
        content.deleteResource(id);
    }

    @GetMapping("/lesson-sessions")
    List<SessionView> listSessions(@RequestParam @Positive long lessonId) {
        return content.listSessions(lessonId);
    }

    @GetMapping("/lesson-sessions/{id}")
    SessionView getSession(@PathVariable @Positive long id) {
        return content.getSession(id);
    }

    @PostMapping("/lesson-sessions")
    ResponseEntity<SessionView> createSession(@RequestParam @Positive long lessonId,
                                        @Valid @RequestBody SessionInput input) {
        SessionView created = content.createSession(lessonId, input);
        return Responses.created(created.id(), created);
    }

    @PutMapping("/lesson-sessions/{id}")
    SessionView updateSession(@PathVariable @Positive long id, @Valid @RequestBody SessionInput input) {
        return content.updateSession(id, input);
    }

    @DeleteMapping("/lesson-sessions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSession(@PathVariable @Positive long id) {
        content.deleteSession(id);
    }
}
