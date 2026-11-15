# Hibernate Academy

Учебный REST API для курса «ORM и Hibernate». В нём можно вести программу и расписание, записывать студентов на курс, проверять задания и проводить тесты.

Java 21, Maven, Spring Boot, Spring Data JPA, Hibernate, PostgreSQL 16 и Liquibase.

## Запуск

Скопируй `.env.example` в `.env` и задай `DB_PASSWORD`. Из папки проекта выполни:

```sh
docker compose up --build -d
```

Когда приложение запустится, открой [список курсов](http://localhost:8080/api/courses). Для логов есть `docker compose logs -f app`, для остановки — `docker compose down`. База хранится в отдельном томе.

Без Docker понадобятся JDK 21, Maven и PostgreSQL. Создай базу и задай `DB_URL` (например, `jdbc:postgresql://localhost:5432/academy`), `DB_USER` и `DB_PASSWORD`. Запусти `mvn spring-boot:run`. Maven скачает библиотеки при первой сборке, Liquibase создаст таблицы, а Hibernate проверит маппинг. Для демоданных поставь `SPRING_PROFILES_ACTIVE=dev`. В Compose этот профиль уже включён.

## Как проверить API

Открой [demo.http](demo.http) в IntelliJ IDEA и выполни запросы сверху вниз. Пример находит демо-курс, показывает программу и расписание, сдаёт и оценивает работу, затем проходит тест. На чистой базе у `student@example.test` есть запись на курс и свободная попытка. У `reviewer@example.test` уже есть оценка и результат теста.

В `X-Actor-Id` передаётся ID студента или преподавателя. Сервис использует его для проверки роли, записи на курс и авторства. В этой версии вход через логин и пароль и браузерный интерфейс отсутствуют.

## REST API

Все маршруты начинаются с `/api`. В таблицах **CRUD** означает `GET` списка и элемента, `POST` в коллекцию, `PUT` и `DELETE` элемента. Параметры родителя передаются при запросе списка и создании.

| Ресурс | Методы | Тело записи JSON |
| --- | --- | --- |
| `/users` | CRUD | `name`, `email`, при создании `role` со значением `STUDENT`, `TEACHER` или `ADMIN` |
| `/users/{id}/profile` | GET, PUT | `bio`, `avatarUrl`, оба поля необязательны |
| `/categories` | CRUD | `name` |
| `/tags` | CRUD | `name` |
| `/courses` | CRUD, фильтр списка `?tagId=` | `title`, `description`, `categoryId`, `teacherId`, `startDate`, `durationWeeks` |
| `/courses/{id}/tags` | GET, PUT | `{"tagIds":[1,2]}`, пустой массив удаляет связи |
| `/courses/{id}/curriculum` | GET | Программа курса с модулями и уроками |
| `/courses/{id}/schedule` | GET | Все занятия курса в порядке начала |
| `/modules?courseId=` | CRUD | `title`, `description`, `position` |
| `/lessons?moduleId=` | CRUD | `title`, `content`, `position` |
| `/lesson-resources?lessonId=` | CRUD | `title`, `url` |
| `/lesson-sessions?lessonId=` | CRUD | `startsAt`, `endsAt`, `location` |

| Маршрут | Методы и правила |
| --- | --- |
| `/courses/{courseId}/enrollments` | GET списка студентов, POST с `{"studentId":2}` |
| `/courses/{courseId}/enrollments/{studentId}` | GET записи, DELETE для отмены записи |
| `/users/{studentId}/enrollments` | GET списка курсов студента |
| `/courses/{courseId}/reviews` | GET отзывов, POST с `{"rating":5,"comment":"Хорошие примеры"}` и ID записанного студента |
| `/courses/{courseId}/reviews/{reviewId}` | GET, PUT и DELETE, изменение и удаление доступны автору |
| `/assignments?lessonId=` | CRUD с полями `title`, `description`, `dueAt`, `maxScore`. Изменения выполняет преподаватель курса |
| `/assignments/{id}/submissions` | POST с `{"content":"Текст решения"}` от студента, GET списка преподавателем |
| `/assignments/{id}/submissions/{submissionId}` | GET преподавателем курса или автором решения |
| `/assignments/{id}/submissions/{submissionId}/grade` | PUT преподавателем с `{"score":95,"feedback":"Разберите запросы"}` |
| `/quizzes?moduleId=` | CRUD с полем `title`. GET элемента возвращает тест с вариантами без ключа. Изменения выполняет преподаватель |
| `/questions?quizId=` | CRUD с полями `text`, `points`, `position`. Изменения выполняет преподаватель |
| `/answer-options?questionId=` | CRUD преподавателем с полями `text`, `correct`, `position` |
| `/quizzes/{id}/attempts` | POST от студента с `{"answers":[{"questionId":1,"optionId":1}]}`, GET списка преподавателем |
| `/quizzes/{id}/attempts/{attemptId}` | GET преподавателем или автором попытки |
| `/quizzes/{id}/result` | GET попытки студента, указанного в `X-Actor-Id` |

Используйте `YYYY-MM-DD` для `startDate` и ISO 8601 с часовым поясом для моментов времени, например `2030-01-10T10:00:00Z`. Позиции начинаются с 1 и уникальны в пределах своего родителя.

Создание возвращает `201 Created` и заголовок `Location`, удаление возвращает `204 No Content`. Ошибки возвращаются в формате `application/problem+json`. `400` означает ошибку запроса, `403` — запрет по правилам сценария, `404` — отсутствие ресурса, `409` — конфликт с состоянием системы. При ошибке валидации тела объект `fields` содержит сообщения по полям.

## Структура проекта

| Пакет | Что в нём |
| --- | --- |
| `accounts` | Пользователи, роли и профили |
| `catalog` | Курсы, модули, уроки, материалы, расписание, теги, запись на курс и отзывы |
| `assignments` | Задания, решения и оценки |
| `quizzes` | Тесты, варианты ответов и результаты |
| `common` | Общая обработка ошибок и вспомогательные настройки |

Контроллеры принимают HTTP-запросы, сервисы проверяют правила и открывают транзакции, репозитории работают с базой. Наружу возвращаются DTO. Например, при сдаче задания `SubmissionService` проверяет запись студента, дедлайн и предыдущую попытку, затем сохраняет решение.

В модели 18 JPA-сущностей. Аккаунт и профиль связаны 1:1, курс и модули — 1:N. У студентов и курсов связь M:N через `Enrollment`, где хранится дата записи. У курсов и тегов связь M:N через таблицу `course_tag`. Повторные записи и попытки запрещены ограничениями уникальности. Работы и результаты сохраняются при отписке от курса.

На модуль приходится один тест. У каждого вопроса один правильный вариант. После первой попытки тест больше нельзя редактировать, чтобы сохранённые ответы и баллы оставались актуальными.

### Ленивая загрузка

Open Session in View выключен. В одном из тестов чтение модулей после закрытия сессии вызывает `LazyInitializationException`. Для программы курса `@EntityGraph` подгружает модули, а отдельный запрос выбирает сразу все уроки. Так число запросов за уроками не зависит от числа модулей.

## Тесты

```sh
mvn -Pquality clean verify
```

- `QuizScorerTest` проверяет подсчёт баллов и неверные варианты ответов.
- `PlatformApiTest` проверяет CRUD, правила записи и сдачи, ограничения базы и ленивую загрузку.
- `DemoDataTest` проверяет заполнение базы демоданными.

Профиль `test` по умолчанию использует H2. Для прогона на PostgreSQL задай `ACADEMY_TEST_DB_URL`, `ACADEMY_TEST_DB_USER` и `ACADEMY_TEST_DB_PASSWORD` для отдельной тестовой базы. GitHub Actions запускает оба прогона и проверяет ответ приложения после старта через Compose.
