INSERT INTO app_user(name, email, role) VALUES
    ('Анна Орлова', 'teacher@example.test', 'TEACHER'),
    ('Иван Соколов', 'student@example.test', 'STUDENT'),
    ('Мария Волкова', 'reviewer@example.test', 'STUDENT');

INSERT INTO user_profile(account_id, bio)
SELECT id, 'Участник демонстрационного курса'
FROM app_user
WHERE email IN ('teacher@example.test', 'student@example.test', 'reviewer@example.test');

INSERT INTO category(name) VALUES ('Программирование');
INSERT INTO tag(name) VALUES ('Hibernate'), ('Java');

INSERT INTO course(title, description, category_id, teacher_id, start_date, duration_weeks)
SELECT 'ORM и Hibernate', 'Связи JPA, транзакции и работа с ленивой загрузкой',
       category.id, app_user.id, CURRENT_DATE, 4
FROM category CROSS JOIN app_user
WHERE category.name = 'Программирование' AND app_user.email = 'teacher@example.test';

INSERT INTO course_tag(course_id, tag_id)
SELECT course.id, tag.id FROM course CROSS JOIN tag
WHERE course.title = 'ORM и Hibernate' AND tag.name IN ('Hibernate', 'Java');

INSERT INTO course_module(course_id, title, description, position)
SELECT id, 'Модель данных', 'Сущности, связи и состояние объектов', 1
FROM course WHERE title = 'ORM и Hibernate';

INSERT INTO lesson(module_id, title, content, position)
SELECT id, 'Ленивая загрузка', 'Изучите разницу между LAZY и EAGER, затем запустите SQL-логирование.', 1
FROM course_module WHERE title = 'Модель данных';

INSERT INTO lesson_resource(lesson_id, title, url)
SELECT id, 'Документация Hibernate ORM', 'https://hibernate.org/orm/documentation/'
FROM lesson WHERE title = 'Ленивая загрузка';

INSERT INTO lesson_session(lesson_id, starts_at, ends_at, location)
SELECT id, CURRENT_TIMESTAMP + INTERVAL '1' DAY,
       CURRENT_TIMESTAMP + INTERVAL '1' DAY + INTERVAL '1' HOUR, 'Онлайн, аудитория 1'
FROM lesson WHERE title = 'Ленивая загрузка';

INSERT INTO enrollment(course_id, student_id, enrolled_at)
SELECT course.id, app_user.id, CURRENT_TIMESTAMP FROM course CROSS JOIN app_user
WHERE course.title = 'ORM и Hibernate' AND app_user.email IN ('student@example.test', 'reviewer@example.test');

INSERT INTO course_review(course_id, student_id, rating, comment, created_at)
SELECT course.id, app_user.id, 5, 'Понравилось разбирать SQL, который генерирует Hibernate.', CURRENT_TIMESTAMP
FROM course CROSS JOIN app_user
WHERE course.title = 'ORM и Hibernate' AND app_user.email = 'reviewer@example.test';

INSERT INTO assignment(lesson_id, title, description, due_at, max_score)
SELECT id, 'Диагностика N+1', 'Покажите запрос с N+1 и исправьте его с помощью fetch join.',
       CURRENT_TIMESTAMP + INTERVAL '30' DAY, 100
FROM lesson WHERE title = 'Ленивая загрузка';

INSERT INTO submission(assignment_id, student_id, submitted_at, content, score, feedback)
SELECT assignment.id, app_user.id, CURRENT_TIMESTAMP, 'Проверил запросы через Hibernate Statistics.',
       90, 'Добавьте сравнение до и после исправления.'
FROM assignment CROSS JOIN app_user
WHERE assignment.title = 'Диагностика N+1' AND app_user.email = 'reviewer@example.test';

INSERT INTO quiz(module_id, title)
SELECT id, 'Проверка знаний ORM' FROM course_module WHERE title = 'Модель данных';

INSERT INTO question(quiz_id, text, points, position)
SELECT id, 'Когда Hibernate загружает ленивую коллекцию?', 10, 1
FROM quiz WHERE title = 'Проверка знаний ORM';

INSERT INTO answer_option(question_id, text, correct, position)
SELECT id, 'При обращении к коллекции в открытой сессии', TRUE, 1 FROM question
WHERE text = 'Когда Hibernate загружает ленивую коллекцию?';

INSERT INTO answer_option(question_id, text, correct, position)
SELECT id, 'Всегда при загрузке родительской сущности', FALSE, 2 FROM question
WHERE text = 'Когда Hibernate загружает ленивую коллекцию?';

INSERT INTO quiz_submission(quiz_id, student_id, taken_at, score, max_score)
SELECT quiz.id, app_user.id, CURRENT_TIMESTAMP, 10, 10 FROM quiz CROSS JOIN app_user
WHERE quiz.title = 'Проверка знаний ORM' AND app_user.email = 'reviewer@example.test';

INSERT INTO quiz_answer(submission_id, question_id, selected_option_id, points_awarded)
SELECT attempt.id, question.id, answer_option.id, 10
FROM quiz_submission attempt
JOIN question ON question.quiz_id = attempt.quiz_id
JOIN answer_option ON answer_option.question_id = question.id AND answer_option.correct
JOIN app_user ON app_user.id = attempt.student_id
WHERE app_user.email = 'reviewer@example.test';
