package com.example.shopapp.responses;

import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse extends BaseResponse {

    private Long id;

    private String name;

    private Float price;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    @JsonProperty("product_images")
    private List<ProductImage> productImages;

    private String description;

    @JsonProperty("category_id")
    private Long categoryId;

    public static ProductResponse fromProduct(Product product) {

        var productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .thumbnailUrl(product.getThumbnailUrl())
                .categoryId(product.getCategory().getId())
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }

    public static ProductResponse fromProduct(Product product, List<ProductImage> productImages) {
        var productResponse = fromProduct(product);
        productResponse.setProductImages(productImages);
        return productResponse;
    }
}
