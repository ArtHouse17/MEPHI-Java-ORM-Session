# Hibernate Academy

Backend учебной платформы для курса «ORM и Hibernate». Преподаватель ведёт курс, публикует задания и составляет тесты. Студент записывается на курс, смотрит программу и расписание, сдаёт работы, проходит тест и получает результаты.

**Стек** — Java 21, Maven, Spring Boot, Spring MVC, Spring Data JPA, Hibernate, PostgreSQL 16 и Liquibase. Для тестов используются JUnit 5, MockMvc и H2. Тот же набор можно запустить с PostgreSQL.

## Запуск через Docker Compose

Нужны Docker и Compose v2.

1. Скопируйте `.env.example` в `.env` и задайте свой `DB_PASSWORD`. Остальные параметры подойдут для локального запуска.
2. Соберите и поднимите сервисы.

   ```sh
   docker compose up --build -d
   docker compose logs -f app
   ```

3. После запуска приложения откройте [список курсов](http://localhost:8080/api/courses) или выполните запросы из [demo.http](demo.http) в HTTP Client IntelliJ IDEA.

Compose ждёт готовности PostgreSQL, запускает приложение с профилем `dev` и публикует API на `127.0.0.1:8080`. PostgreSQL хранит данные в именованном томе `academy-db`. Для остановки используйте `docker compose down`.

## Запуск без Docker

Нужны JDK 21, Maven 3.6.3+ и доступная база PostgreSQL. Создайте пустую базу и пользователя с правом создавать таблицы в её схеме. Передайте настройки окружения.

| Переменная | Значение |
| --- | --- |
| `DB_URL` | JDBC URL, например `jdbc:postgresql://localhost:5432/academy` |
| `DB_USER` | Имя пользователя PostgreSQL |
| `DB_PASSWORD` | Пароль пользователя PostgreSQL |
| `SPRING_PROFILES_ACTIVE` | `dev` для установки демонстрационных данных |

```sh
mvn spring-boot:run
```

Maven скачает зависимости при первой сборке. Liquibase создаёт схему на старте, Hibernate сверяет маппинги со схемой через `ddl-auto: validate`. Профиль `dev` добавляет демонстрационные данные один раз. При стандартном запуске применяется только схема.

## Проверка сценария

На чистой базе профиль `dev` создаёт курс «ORM и Hibernate», модуль, урок, занятие в расписании, материал, теги, задание и тест. В базе есть преподаватель `teacher@example.test` и два записанных студента. У `student@example.test` ещё нет попыток, у `reviewer@example.test` есть оценённая работа и результат теста.

1. Получите ID участников через `GET /api/users` и ID курса через `GET /api/courses`.
2. Откройте программу `GET /api/courses/{id}/curriculum` и расписание `GET /api/courses/{id}/schedule`.
3. Найдите задание через `GET /api/assignments?lessonId=…` и сдайте его с ID студента в `X-Actor-Id`. Оцените ответ с ID преподавателя.
4. Найдите тест через `GET /api/quizzes?moduleId=…`. Получите вопросы и варианты через `GET /api/quizzes/{id}`, отправьте ответы и запросите результат.
5. Повторите отправку работы или теста для того же студента. API вернёт `409 Conflict`.

В учебном API `X-Actor-Id` передаёт ID участника для проверки записи на курс, авторства и принадлежности курса преподавателю. Аутентификация и браузерный интерфейс находятся вне объёма этой версии.

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

## Архитектура и модель данных

Внутри каждого предметного модуля контроллер отвечает за HTTP, DTO задают контракт и валидацию, сервисы управляют правилами и транзакциями, репозитории выполняют запросы, JPA-сущности описывают состояние и связи. Контроллеры работают с DTO, а преобразование сущностей выполняется внутри транзакций.

| Модуль | Зона ответственности | JPA-сущности |
| --- | --- | --- |
| `accounts` | Управление пользователями, профилями и ролями | UserAccount, UserProfile |
| `catalog` | Рубрикатор и теги, программа курса, уроки и материалы, расписание, запись и отзывы | Category, Tag, Course, CourseModule, Lesson, LessonSession, LessonResource, Enrollment, CourseReview |
| `assignments` | Управление заданиями. Сдача, чтение и оценивание решений выделены в отдельный сервис | Assignment, Submission |
| `quizzes` | Редактирование тестов, приём ответов, начисление баллов и чтение результатов | Quiz, Question, AnswerOption, QuizSubmission, QuizAnswer |
| `common` | Ошибки HTTP, заголовок Location и системное время | — |

Модель содержит **18 JPA-сущностей**. Связь `UserAccount ↔ UserProfile` имеет тип 1:1, профиль сохраняется каскадно. Курс содержит модули, модуль содержит уроки. Теги образуют M:N через `course_tag`. Запись студента на курс также имеет кардинальность M:N, но моделируется сущностью `Enrollment` с датой записи и уникальной парой `(course_id, student_id)`.

В модуле допускается один тест с вопросами с одним правильным ответом. Студент выбирает ровно один вариант для каждого вопроса. Сервис начисляет баллы, а транзакция сохраняет результат и варианты ответов. После первой попытки тест доступен для чтения и закрыт для редактирования. Отмена записи сохраняет историю попыток. Внешние ключи защищают учебные работы и результаты при попытке удалить связанный контент.

### Учебный сценарий Lazy Loading

В приложении выключен Open Session in View. В тесте `demonstratesDetachedLazyCollectionAndFetchGraph` обращение к коллекции модулей после обычного `findById` вне сессии вызывает `LazyInitializationException`. Целевой запрос `findCurriculumById` загружает модули и шапку курса через `@EntityGraph`. Уроки для этих модулей сервис получает одним отдельным запросом, поэтому число запросов за уроками не растёт с количеством модулей.

## Тестирование и CI

```sh
mvn -Pquality clean verify
```

- `QuizScorerTest` проверяет вычисление баллов и некорректные комбинации ответов.
- `PlatformApiTest` запускает Spring, Hibernate и Liquibase. Проверяет REST CRUD, ограничения PostgreSQL-совместимой схемы, сценарии обучения и Lazy Loading.
- `DemoDataTest` выполняет демонстрационную миграцию и проверяет наличие данных для основных сценариев.

По умолчанию профиль `test` использует временную H2 в режиме совместимости с PostgreSQL. Для того же прогона на PostgreSQL укажите через `ACADEMY_TEST_DB_URL`, `ACADEMY_TEST_DB_USER` и `ACADEMY_TEST_DB_PASSWORD` отдельную тестовую базу. Тесты записывают в неё данные, в том числе загружают демо-миграцию.

GitHub Actions запускает проверку исходников компилятором, выполняет тесты с обеими СУБД, поднимает стек через Compose и проверяет ответ API. Для сдачи добавьте содержимое этой папки в корень репозитория GitHub и передайте ссылку после успешного прогона CI.
