package com.example.shopapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonProperty("product")
    private Product product;

    @Column(name = "price", nullable = false)
    @DecimalMin(value = "0.00", message = "Price must be greater or equal to 0")
    @JsonProperty("price")
    private Float price;

    @Column(name = "number_of_products", nullable = false)
    @Positive
    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @Column(name = "total_money", nullable = false)
    @DecimalMin(value = "0.00", message = "Total money must be greater or equal to 0")
    @JsonProperty("total_money")
    private Float totalMoney;

    @Column(name = "color", length = 20)
    @JsonProperty("color")
    private String color;
}
