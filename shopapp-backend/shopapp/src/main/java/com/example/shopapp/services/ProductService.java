package com.example.shopapp.services;

import com.example.shopapp.dtos.ProductDTO;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;
import com.example.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    Product createProduct(ProductDTO productDTO);

    Product getProductById(Long id);

    List<Product> getProductsByIds(List<Long> productIds);

    List<ProductImage> getAllProductImages(Long productId);

    Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest);

    Product updateProduct(Long id, ProductDTO productDTO);

    void deleteProduct(Long id);

    boolean existsByName(String name);

    ResponseEntity<?> validateAndStoreFiles(Long productId, List<MultipartFile> files) throws Exception;
}
