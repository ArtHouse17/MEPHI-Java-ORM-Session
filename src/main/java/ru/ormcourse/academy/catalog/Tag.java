package ru.ormcourse.academy.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ru.ormcourse.academy.common.IdentifiedEntity;

@Entity
@Table(name = "tag")
public class Tag extends IdentifiedEntity {
    @Column(nullable = false, unique = true, length = 80)
    private String name;

    protected Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void update(String name) {
        this.name = name;
    }
}
