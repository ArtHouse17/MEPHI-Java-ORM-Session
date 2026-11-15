package ru.ormcourse.academy.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.ormcourse.academy.dto.CatalogDtos.CategoryInput;
import ru.ormcourse.academy.dto.CatalogDtos.CategoryView;
import ru.ormcourse.academy.dto.CatalogDtos.TagInput;
import ru.ormcourse.academy.dto.CatalogDtos.TagView;
import ru.ormcourse.academy.service.TaxonomyService;

@RestController
@RequestMapping("/api")
public class TaxonomyController {
    private final TaxonomyService taxonomy;

    public TaxonomyController(TaxonomyService taxonomy) {
        this.taxonomy = taxonomy;
    }

    @GetMapping("/categories")
    List<CategoryView> listCategories() {
        return taxonomy.listCategories();
    }

    @GetMapping("/categories/{id}")
    CategoryView getCategory(@PathVariable @Positive long id) {
        return taxonomy.getCategory(id);
    }

    @PostMapping("/categories")
    ResponseEntity<CategoryView> createCategory(@Valid @RequestBody CategoryInput input) {
        CategoryView created = taxonomy.createCategory(input);
        return Responses.created(created.id(), created);
    }

    @PutMapping("/categories/{id}")
    CategoryView updateCategory(@PathVariable @Positive long id, @Valid @RequestBody CategoryInput input) {
        return taxonomy.updateCategory(id, input);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable @Positive long id) {
        taxonomy.deleteCategory(id);
    }

    @GetMapping("/tags")
    List<TagView> listTags() {
        return taxonomy.listTags();
    }

    @GetMapping("/tags/{id}")
    TagView getTag(@PathVariable @Positive long id) {
        return taxonomy.getTag(id);
    }

    @PostMapping("/tags")
    ResponseEntity<TagView> createTag(@Valid @RequestBody TagInput input) {
        TagView created = taxonomy.createTag(input);
        return Responses.created(created.id(), created);
    }

    @PutMapping("/tags/{id}")
    TagView updateTag(@PathVariable @Positive long id, @Valid @RequestBody TagInput input) {
        return taxonomy.updateTag(id, input);
    }

    @DeleteMapping("/tags/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTag(@PathVariable @Positive long id) {
        taxonomy.deleteTag(id);
    }
}
