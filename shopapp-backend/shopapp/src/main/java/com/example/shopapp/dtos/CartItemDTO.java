package com.example.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("quantity")
    private int quantity;
}
