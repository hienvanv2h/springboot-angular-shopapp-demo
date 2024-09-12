package com.example.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @Min(value = 1, message = "User ID must be greater than 0")
    @JsonProperty("user_id")
    private Long userId;

    @NotBlank(message = "Full name is required")
    @JsonProperty("fullname")
    private String fullName;

    private String email;

    @NotBlank(message = "Phone number is required")
    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;

    private String note;

    @Min(value = 0, message = "Total money cannot be negative")
    @JsonProperty("total_money")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @NotBlank(message = "Shipping address cannot be blank")
    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;
}
