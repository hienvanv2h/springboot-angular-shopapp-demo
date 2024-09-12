package com.example.shopapp.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDTO {

    @NotEmpty(message = "Category name cannot be empty")
    private String name;
}
