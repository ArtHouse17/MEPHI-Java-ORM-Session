package ru.ormcourse.academy.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByOrderByIdAsc();

}
