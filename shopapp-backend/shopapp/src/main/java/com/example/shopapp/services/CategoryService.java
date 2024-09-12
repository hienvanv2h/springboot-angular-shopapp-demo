package com.example.shopapp.services;

import com.example.shopapp.dtos.CategoryDTO;
import com.example.shopapp.models.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(CategoryDTO categoryDTO);

    List<Category> getAllCategories();

    Category getCategoryById(Long id);

    Category updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);
}
