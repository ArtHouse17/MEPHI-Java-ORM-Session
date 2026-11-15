package ru.ormcourse.academy.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ormcourse.academy.dto.CatalogDtos.CategoryInput;
import ru.ormcourse.academy.dto.CatalogDtos.CategoryView;
import ru.ormcourse.academy.dto.CatalogDtos.TagInput;
import ru.ormcourse.academy.dto.CatalogDtos.TagView;
import ru.ormcourse.academy.entity.Category;
import ru.ormcourse.academy.entity.Tag;
import ru.ormcourse.academy.exception.ApiException;
import ru.ormcourse.academy.repository.CategoryRepository;
import ru.ormcourse.academy.repository.TagRepository;

@Service
@Transactional(readOnly = true)
public class TaxonomyService {
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public TaxonomyService(CategoryRepository categoryRepository,
                          TagRepository tagRepository) {
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    public List<CategoryView> listCategories() {
        return categoryRepository.findAllByOrderByIdAsc()
                .stream().map(CategoryView::from).toList();
    }

    public CategoryView getCategory(long id) {
        return CategoryView.from(requireCategory(id));
    }

    @Transactional
    public CategoryView createCategory(CategoryInput input) {
        Category category = new Category(input.name().strip());
        return CategoryView.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryView updateCategory(long id, CategoryInput input) {
        Category category = requireCategory(id);
        category.update(input.name().strip());
        return CategoryView.from(category);
    }

    @Transactional
    public void deleteCategory(long id) {
        categoryRepository.delete(requireCategory(id));
    }

    private Category requireCategory(long id) {
        return ApiException.require(categoryRepository.findById(id), "Category", id);
    }

    public List<TagView> listTags() {
        return tagRepository.findAllByOrderByIdAsc()
                .stream().map(TagView::from).toList();
    }

    public TagView getTag(long id) {
        return TagView.from(requireTag(id));
    }

    @Transactional
    public TagView createTag(TagInput input) {
        Tag tag = new Tag(input.name().strip());
        return TagView.from(tagRepository.save(tag));
    }

    @Transactional
    public TagView updateTag(long id, TagInput input) {
        Tag tag = requireTag(id);
        tag.update(input.name().strip());
        return TagView.from(tag);
    }

    @Transactional
    public void deleteTag(long id) {
        tagRepository.delete(requireTag(id));
    }

    private Tag requireTag(long id) {
        return ApiException.require(tagRepository.findById(id), "Tag", id);
    }
}
