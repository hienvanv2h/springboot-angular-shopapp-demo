package com.example.shopapp.dtos;

import com.example.shopapp.models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product ID must be > 0")
    private Long productId;

    @JsonProperty("image_url")
    @Size(min = 5, max = 300, message = "Image URL")
    private String imageUrl;
}
