package ru.ormcourse.academy.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ru.ormcourse.academy.accounts.UserAccount;
import ru.ormcourse.academy.common.IdentifiedEntity;

@Entity
@Table(name = "course")
public class Course extends IdentifiedEntity {
    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private UserAccount teacher;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private int durationWeeks;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    @OrderBy("position, id")
    private List<CourseModule> modules = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "course_tag", joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    protected Course() {
    }

    public Course(String title, String description, Category category, UserAccount teacher, LocalDate startDate, int durationWeeks) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.teacher = teacher;
        this.startDate = startDate;
        this.durationWeeks = durationWeeks;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public UserAccount getTeacher() {
        return teacher;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getDurationWeeks() {
        return durationWeeks;
    }

    public List<CourseModule> getModules() {
        return modules;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void update(String title, String description, Category category, UserAccount teacher, LocalDate startDate, int durationWeeks) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.teacher = teacher;
        this.startDate = startDate;
        this.durationWeeks = durationWeeks;
    }

    public void replaceTags(Collection<Tag> newTags) {
        tags.clear();
        tags.addAll(newTags);
    }
}
