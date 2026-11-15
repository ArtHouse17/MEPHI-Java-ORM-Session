package ru.ormcourse.academy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.liquibase.contexts=dev")
@ActiveProfiles("test")
class DemoDataTest {
    @Autowired
    JdbcTemplate jdbc;

    @Test
    void installsDemoDataForTheWholeLearningScenario() {
        assertThat(count("app_user")).isGreaterThanOrEqualTo(3);
        assertThat(jdbc.queryForList("select title from course", String.class))
                .contains("ORM-фреймворки для Java");
        assertThat(count("course_tag")).isGreaterThanOrEqualTo(2);
        assertThat(count("lesson_resource")).isPositive();
        assertThat(count("lesson_session")).isPositive();
        assertThat(count("enrollment")).isGreaterThanOrEqualTo(2);
        assertThat(count("course_review")).isPositive();
        assertThat(count("submission")).isPositive();
        assertThat(count("quiz_answer")).isPositive();
    }

    private long count(String table) {
        return jdbc.queryForObject("select count(*) from " + table, Long.class);
    }
}
