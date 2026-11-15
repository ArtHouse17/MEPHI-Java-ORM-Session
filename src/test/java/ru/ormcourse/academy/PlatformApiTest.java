package ru.ormcourse.academy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.ormcourse.academy.entity.Course;
import ru.ormcourse.academy.repository.CourseRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlatformApiTest {
    private static final String ACTOR = "X-Actor-Id";
    private static final String USERS = "/api/users";
    private static final String CATEGORIES = "/api/categories";
    private static final String COURSES = "/api/courses";
    private static final String MODULES = "/api/modules";
    private static final String LESSONS = "/api/lessons";
    private static final String ASSIGNMENTS = "/api/assignments";
    private static final String QUIZZES = "/api/quizzes";
    private static final String QUESTIONS = "/api/questions";
    private static final String OPTIONS = "/api/answer-options";

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CourseRepository courses;

    long teacherId;
    long studentId;
    long categoryId;
    long courseId;
    long moduleId;
    long lessonId;

    @BeforeEach
    void createCourse() throws Exception {
        teacherId = createUser("TEACHER");
        studentId = createUser("STUDENT");
        categoryId = create(CATEGORIES, Map.of("name", unique("Programming")));
        courseId = create(COURSES, course("Hibernate", teacherId));
        moduleId = create(MODULES + "?courseId=" + courseId, module("JPA", 1));
        lessonId = create(LESSONS + "?moduleId=" + moduleId, lesson("Fetching"));
    }

    @Test
    void createsReadsUpdatesAndDeletesUserWithProfile() throws Exception {
        long id = createUser("STUDENT");
        mvc.perform(get(USERS + "/" + id + "/profile")).andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(id));
        mvc.perform(json(put(USERS + "/" + id + "/profile"), Map.of("bio", "Learning ORM")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.bio").value("Learning ORM"));
        mvc.perform(json(put(USERS + "/" + id), Map.of("name", "Updated", "email", uniqueEmail())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Updated"));
        mvc.perform(get(USERS + "/" + id + "/profile")).andExpect(jsonPath("$.bio").value("Learning ORM"));
        mvc.perform(delete(USERS + "/" + id)).andExpect(status().isNoContent());
        mvc.perform(get(USERS + "/" + id)).andExpect(status().isNotFound());
        mvc.perform(get(USERS + "/" + id + "/profile")).andExpect(status().isNotFound());
    }

    @Test
    void reportsDuplicateEmailIgnoringCase() throws Exception {
        String email = uniqueEmail();
        create(USERS, Map.of("name", "First", "email", email, "role", "STUDENT"));
        mvc.perform(json(post(USERS), Map.of("name", "Second", "email", email.toUpperCase(java.util.Locale.ROOT),
                        "role", "TEACHER")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void validatesJsonAndPathParameters() throws Exception {
        mvc.perform(json(post(USERS), Map.of("name", "", "email", "bad", "role", "STUDENT")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.name").exists())
                .andExpect(jsonPath("$.fields.email").exists());
        mvc.perform(get(COURSES + "/-1")).andExpect(status().isBadRequest());
        mvc.perform(post(USERS).contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void managesCourseAndReturnsItsCurriculum() throws Exception {
        mvc.perform(json(put(coursePath()), course("Hibernate and PostgreSQL", teacherId)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Hibernate and PostgreSQL"));
        mvc.perform(get(coursePath() + "/curriculum")).andExpect(status().isOk())
                .andExpect(jsonPath("$.course.categoryId").value(categoryId))
                .andExpect(jsonPath("$.course.teacherId").value(teacherId))
                .andExpect(jsonPath("$.modules[0].module.id").value(moduleId))
                .andExpect(jsonPath("$.modules[0].lessons[0].id").value(lessonId));
        mvc.perform(get(coursePath())).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Hibernate and PostgreSQL"));
    }

    @Test
    void rejectsStudentAsCourseTeacher() throws Exception {
        mvc.perform(json(post(COURSES), course("Invalid teacher", studentId)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void changesCategoryAndRejectsDeletionWhileUsed() throws Exception {
        String name = unique("Backend");
        mvc.perform(json(put(CATEGORIES + "/" + categoryId), Map.of("name", name)))
                .andExpect(status().isOk());
        mvc.perform(get(coursePath())).andExpect(jsonPath("$.category").value(name));
        mvc.perform(delete(CATEGORIES + "/" + categoryId)).andExpect(status().isConflict());
        mvc.perform(delete(coursePath())).andExpect(status().isNoContent());
        mvc.perform(delete(CATEGORIES + "/" + categoryId)).andExpect(status().isNoContent());
    }

    @Test
    void managesModulesLessonsAndResourcesAndCascadesRemoval() throws Exception {
        long resourceId = create("/api/lesson-resources?lessonId=" + lessonId,
                Map.of("title", "Notes", "url", "https://example.test/notes"));
        mvc.perform(json(put(MODULES + "/" + moduleId), module("Updated module", 2)))
                .andExpect(status().isOk());
        mvc.perform(json(put(LESSONS + "/" + lessonId), lesson("Updated lesson"))).andExpect(status().isOk());
        mvc.perform(get("/api/lesson-resources/" + resourceId)).andExpect(status().isOk());
        mvc.perform(json(put("/api/lesson-resources/" + resourceId),
                        Map.of("title", "Updated", "url", "https://example.test/update")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Updated"));
        mvc.perform(delete(MODULES + "/" + moduleId)).andExpect(status().isNoContent());
        mvc.perform(get(LESSONS + "/" + lessonId)).andExpect(status().isNotFound());
        mvc.perform(get("/api/lesson-resources/" + resourceId)).andExpect(status().isNotFound());
        mvc.perform(get(coursePath() + "/curriculum")).andExpect(jsonPath("$.modules").isEmpty());
    }

    @Test
    void schedulesLessonsAndValidatesTimeOrder() throws Exception {
        String path = "/api/lesson-sessions?lessonId=" + lessonId;
        long sessionId = create(path, session("2030-01-10T10:00:00Z", "2030-01-10T11:00:00Z"));
        mvc.perform(json(post(path), session("2030-01-10T11:00:00Z", "2030-01-10T10:00:00Z")))
                .andExpect(status().isBadRequest());
        mvc.perform(get(coursePath() + "/schedule")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sessionId))
                .andExpect(jsonPath("$[0].lessonTitle").value("Fetching"));
        mvc.perform(delete("/api/lesson-sessions/" + sessionId)).andExpect(status().isNoContent());
        mvc.perform(get(coursePath() + "/schedule")).andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void filtersCoursesByTagAndRemovesLinks() throws Exception {
        long tagId = create("/api/tags", Map.of("name", unique("Hibernate")));
        mvc.perform(json(put(coursePath() + "/tags"), Map.of("tagIds", List.of(tagId))))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(tagId));
        mvc.perform(get(COURSES).param("tagId", Long.toString(tagId))).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(courseId));
        mvc.perform(delete("/api/tags/" + tagId)).andExpect(status().isConflict());
        mvc.perform(json(put(coursePath() + "/tags"), Map.of("tagIds", List.of())))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
        mvc.perform(delete("/api/tags/" + tagId)).andExpect(status().isNoContent());
    }

    @Test
    void enrollsListsRejectsDuplicatesAndWithdraws() throws Exception {
        enroll();
        mvc.perform(get(coursePath() + "/enrollments")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(studentId));
        mvc.perform(get(USERS + "/" + studentId + "/enrollments")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseId").value(courseId));
        mvc.perform(json(post(coursePath() + "/enrollments"), Map.of("studentId", studentId)))
                .andExpect(status().isConflict());
        String member = coursePath() + "/enrollments/" + studentId;
        mvc.perform(get(member)).andExpect(status().isOk());
        mvc.perform(delete(member)).andExpect(status().isNoContent());
        mvc.perform(get(member)).andExpect(status().isNotFound());
    }

    @Test
    void createsUpdatesAndDeletesOwnCourseReview() throws Exception {
        enroll();
        String reviews = coursePath() + "/reviews";
        long id = create(reviews, Map.of("rating", 5, "comment", "Clear"), studentId);
        mvc.perform(get(reviews)).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(json(put(reviews + "/" + id).header(ACTOR, studentId),
                        Map.of("rating", 4, "comment", "More examples")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.rating").value(4));
        mvc.perform(delete(reviews + "/" + id).header(ACTOR, teacherId)).andExpect(status().isForbidden());
        mvc.perform(delete(reviews + "/" + id).header(ACTOR, studentId)).andExpect(status().isNoContent());
        mvc.perform(get(reviews + "/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void guardsAssignmentSubmissionAndSupportsTeacherGrading() throws Exception {
        long assignmentId = createAssignment(Instant.now().plus(1, ChronoUnit.DAYS));
        String assignment = ASSIGNMENTS + "/" + assignmentId;
        String submissions = assignment + "/submissions";
        Map<String, String> input = Map.of("content", "Mapped associations");
        mvc.perform(json(post(submissions).header(ACTOR, studentId), input)).andExpect(status().isForbidden());
        enroll();
        long submissionId = create(submissions, input, studentId);
        mvc.perform(json(post(submissions).header(ACTOR, studentId), input)).andExpect(status().isConflict());
        mvc.perform(get(submissions).header(ACTOR, teacherId)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(submissionId));
        String resource = submissions + "/" + submissionId;
        mvc.perform(get(resource).header(ACTOR, studentId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Mapped associations"));
        mvc.perform(json(put(resource + "/grade").header(ACTOR, teacherId), Map.of("score", 101)))
                .andExpect(status().isBadRequest());
        mvc.perform(json(put(resource + "/grade").header(ACTOR, teacherId), Map.of("score", 93, "feedback", "Good")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.score").value(93));
        mvc.perform(get(resource).header(ACTOR, studentId)).andExpect(jsonPath("$.feedback").value("Good"));
        mvc.perform(delete(coursePath())).andExpect(status().isConflict());
        mvc.perform(get(LESSONS + "/" + lessonId)).andExpect(status().isOk());
    }

    @Test
    void refusesLateSolutionsAndNonTeacherAssignmentCreation() throws Exception {
        enroll();
        long assignmentId = createAssignment(Instant.now().minus(1, ChronoUnit.DAYS));
        mvc.perform(json(post(ASSIGNMENTS + "?lessonId=" + lessonId).header(ACTOR, studentId),
                        assignment(Instant.now().plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isForbidden());
        mvc.perform(json(post(ASSIGNMENTS + "/" + assignmentId + "/submissions").header(ACTOR, studentId),
                        Map.of("content", "Too late")))
                .andExpect(status().isConflict());
    }

    @Test
    void persistsQuizScoresAndAnswersAndFreezesTheQuestions() throws Exception {
        enroll();
        long quizId = create(QUIZZES + "?moduleId=" + moduleId, Map.of("title", "ORM quiz"), teacherId);
        long questionId = create(QUESTIONS + "?quizId=" + quizId,
                Map.of("text", "Which option?", "points", 10, "position", 1), teacherId);
        long correct = create(OPTIONS + "?questionId=" + questionId,
                Map.of("text", "Correct", "correct", true, "position", 1), teacherId);
        create(OPTIONS + "?questionId=" + questionId,
                Map.of("text", "Incorrect", "correct", false, "position", 2), teacherId);
        String quiz = QUIZZES + "/" + quizId;
        mvc.perform(get(quiz)).andExpect(status().isOk())
                .andExpect(jsonPath("$.questions[0].options[0].id").value(correct))
                .andExpect(jsonPath("$.questions[0].options[0].correct").doesNotExist());
        mvc.perform(get(OPTIONS + "/" + correct).header(ACTOR, studentId)).andExpect(status().isForbidden());
        Map<String, Object> wrong = Map.of("answers", List.of(Map.of("questionId", questionId, "optionId", 999999)));
        mvc.perform(json(post(quiz + "/attempts").header(ACTOR, studentId), wrong)).andExpect(status().isBadRequest());
        Map<String, Object> input = Map.of("answers", List.of(Map.of("questionId", questionId, "optionId", correct)));
        mvc.perform(json(post(quiz + "/attempts").header(ACTOR, studentId), input)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.attempt.score").value(10)).andExpect(jsonPath("$.attempt.maxScore").value(10));
        mvc.perform(get(quiz + "/result").header(ACTOR, studentId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.answers[0].optionId").value(correct))
                .andExpect(jsonPath("$.answers[0].pointsAwarded").value(10));
        mvc.perform(get(quiz + "/attempts").header(ACTOR, teacherId)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(studentId));
        mvc.perform(json(post(quiz + "/attempts").header(ACTOR, studentId), input)).andExpect(status().isConflict());
        mvc.perform(delete(QUESTIONS + "/" + questionId).header(ACTOR, teacherId)).andExpect(status().isConflict());
    }

    @Test
    void demonstratesDetachedLazyCollectionAndFetchGraph() {
        Course detached = courses.findById(courseId).orElseThrow();
        Executable accessDetachedCollection = () -> detached.getModules().size();
        org.junit.jupiter.api.Assertions.assertThrows(LazyInitializationException.class, accessDetachedCollection);

        Course loaded = courses.findCurriculumById(courseId).orElseThrow();
        assertThat(loaded.getModules()).extracting(module -> module.getId()).containsExactly(moduleId);
    }

    private long createUser(String role) throws Exception {
        return create(USERS, Map.of("name", role, "email", uniqueEmail(), "role", role));
    }

    private void enroll() throws Exception {
        mvc.perform(json(post(coursePath() + "/enrollments"), Map.of("studentId", studentId)))
                .andExpect(status().isCreated());
    }

    private long createAssignment(Instant dueAt) throws Exception {
        return create(ASSIGNMENTS + "?lessonId=" + lessonId, assignment(dueAt), teacherId);
    }

    private Map<String, Object> assignment(Instant dueAt) {
        return Map.of("title", "Fetch tasks", "description", "Solve the N+1 query",
                "dueAt", dueAt.toString(), "maxScore", 100);
    }

    private Map<String, Object> course(String title, long teacher) {
        return Map.of("title", title, "description", "ORM training", "categoryId", categoryId,
                "teacherId", teacher, "startDate", "2030-01-10", "durationWeeks", 4);
    }

    private Map<String, Object> module(String title, int position) {
        return Map.of("title", title, "description", "Persistence module", "position", position);
    }

    private Map<String, Object> lesson(String title) {
        return Map.of("title", title, "content", "Fetch strategies", "position", 1);
    }

    private Map<String, String> session(String start, String end) {
        return Map.of("startsAt", start, "endsAt", end, "location", "Online");
    }

    private long create(String uri, Map<String, ?> payload) throws Exception {
        return create(post(uri), payload);
    }

    private long create(String uri, Map<String, ?> payload, long actor) throws Exception {
        return create(post(uri).header(ACTOR, actor), payload);
    }

    private long create(MockHttpServletRequestBuilder request, Map<String, ?> payload) throws Exception {
        byte[] response = mvc.perform(json(request, payload)).andExpect(status().isCreated())
                .andExpect(header().exists("Location")).andReturn().getResponse().getContentAsByteArray();
        JsonNode body = mapper.readTree(response);
        assertThat(body.hasNonNull("id")).isTrue();
        return body.get("id").asLong();
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder request, Map<String, ?> payload)
            throws Exception {
        return request.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(payload));
    }

    private String coursePath() {
        return COURSES + "/" + courseId;
    }

    private static String unique(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }

    private static String uniqueEmail() {
        return unique("user") + "@example.test";
    }
}
