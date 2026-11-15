package ru.ormcourse.academy.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ru.ormcourse.academy.common.IdentifiedEntity;

@Entity
@Table(name = "category")
public class Category extends IdentifiedEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    protected Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void update(String name) {
        this.name = name;
    }
}
