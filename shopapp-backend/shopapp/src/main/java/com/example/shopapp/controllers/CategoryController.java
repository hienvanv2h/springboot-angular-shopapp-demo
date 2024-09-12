package com.example.shopapp.controllers;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.CategoryDTO;
import com.example.shopapp.models.Category;
import com.example.shopapp.responses.GenericResponse;
import com.example.shopapp.services.CategoryService;
import com.example.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                            BindingResult result) {
        if(result.hasErrors()) {
            var errorMessages = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(
                    GenericResponse.getErrorMessages(errorMessages)
            );
        }

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .data(categoryService.createCategory(categoryDTO))
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_CREATE_SUCCESS))
                        .build()
        );
    }

    private final CategoryService categoryService;
    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result
    ) {
        if(result.hasErrors()) {
            var errorMessages = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(
                    GenericResponse.getErrorMessages(errorMessages)
            );
        }
        Category updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse> deleteCategory(@PathVariable @Min(1) Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                GenericResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_DELETE_SUCCESS, id))
                        .build()
        );
    }
}
