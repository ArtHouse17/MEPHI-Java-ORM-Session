package ru.ormcourse.academy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ormcourse.academy.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByOrderByIdAsc();

}
