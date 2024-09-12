package com.example.shopapp.services.impls;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.CategoryDTO;
import com.example.shopapp.exceptionhandling.DataNotFoundException;
import com.example.shopapp.models.Category;
import com.example.shopapp.repositories.CategoryRepository;
import com.example.shopapp.services.CategoryService;
import com.example.shopapp.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) {
        Category newCategory = Category.builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(
                        () -> new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, id)
                        )
                );
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category existingCategory = categoryRepository.findById(id).orElse(null);
        if (existingCategory != null) {
            categoryRepository.deleteById(id);
        } else {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, id)
            );
        }
    }
}
