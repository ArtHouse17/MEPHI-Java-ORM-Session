package ru.ormcourse.academy.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.ormcourse.academy.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByModuleId(long moduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Quiz e where e.id = :id")
    Optional<Quiz> findForUpdate(long id);

}
