package ru.ormcourse.academy.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ormcourse.academy.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    List<UserAccount> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = "profile")
    Optional<UserAccount> findWithProfileById(long id);

}
