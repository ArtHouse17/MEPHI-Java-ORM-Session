package ru.ormcourse.academy.quizzes;

import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByModuleId(long moduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Quiz e where e.id = :id")
    Optional<Quiz> findForUpdate(long id);

}
