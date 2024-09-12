package com.example.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {

    @Min(value = 1, message = "Order ID must be greater than 0")
    @JsonProperty("order_id")
    private Long orderId;

    @Min(value = 1, message = "Product ID must be greater than 0")
    @JsonProperty("product_id")
    private Long productId;

    @Min(value = 0, message = "Price must be greater or equal to 0")
    private Float price;

    @Min(value = 1, message = "Number of products >= 1")
    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @Min(value = 0, message = "Total money must be greater or equal to 0")
    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;
}
