package com.example.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {

    private List<ProductResponse> products;

    @JsonProperty("total_pages")
    private int totalPages;
}
