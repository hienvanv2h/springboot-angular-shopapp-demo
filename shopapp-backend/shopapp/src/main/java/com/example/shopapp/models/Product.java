package com.example.shopapp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 350)
    private String name;

    @Column(name = "price", nullable = false)
    @DecimalMin(value = "0.00", message = "Price must be greater or equal to 0")
    private Float price;

    @Column(name = "thumbnail_url", length = 300)
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
