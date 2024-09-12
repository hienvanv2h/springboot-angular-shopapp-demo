package com.example.shopapp.controllers;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.ProductDTO;
import com.example.shopapp.models.Product;
import com.example.shopapp.responses.GenericResponse;
import com.example.shopapp.responses.ProductListResponse;
import com.example.shopapp.responses.ProductResponse;
import com.example.shopapp.services.ProductService;
import com.example.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO request,
            BindingResult result
    ) {
        if(result.hasErrors()) {
            var errorMessages = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(
                    GenericResponse.getErrorMessages(errorMessages)
            );
        }
        // Add new product to db
        Product newProduct = productService.createProduct(request);

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .data(newProduct)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_CREATE_SUCCESS))
                        .build()
        );
    }

    // Endpoint for uploading image
    @PostMapping(value = "/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files
    ) throws Exception {
        Product existingProduct = productService.getProductById(productId);
        // Validates files and stores if valid
        return productService.validateAndStoreFiles(existingProduct.getId(), files);
    }

    // Endpoint to view image
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get("uploads", imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if(resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                resource = new UrlResource(Paths.get("uploads/not-found.jpg").toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "category_id", defaultValue = "0") Long categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(
                page,
                limit,
//                Sort.by("createdAt").descending()
                Sort.by("id").ascending()
        );
        Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId, pageRequest);
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(
                ProductListResponse.builder()
                        .products(products)
                        .totalPages(totalPages)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product existingProduct = productService.getProductById(id);
        var productImages = productService.getAllProductImages(id);
        var productResponse = ProductResponse.fromProduct(existingProduct, productImages);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/by-ids")
    public ResponseEntity<List<ProductResponse>> getProductsByIds(
            @RequestParam("ids") String ids
    ) {
        // ids=1,2,3,5,9
        var productIds = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        List<ProductResponse> responses = productService.getProductsByIds(productIds).stream()
                .map(ProductResponse::fromProduct)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductById(
            @PathVariable Long id,
            @RequestBody ProductDTO request
    ) {
        Product existingProduct = productService.updateProduct(id, request);
        var productResponse = ProductResponse.fromProduct(existingProduct);
        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_DELETE_SUCCESS, id)
        );
    }

    /*
    @PostMapping("/generateFakeData")
    public ResponseEntity<String> generateFakeData() {
        Faker faker = new Faker();
        for(int i = 0; i < 1000; i++) {
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float) faker.number().randomDouble(2, 0, 10000))
                    .thumbnailUrl("")
                    .description(faker.lorem().sentence())
                    .categoryId((long) faker.number().numberBetween(1, 3))
                    .build();
            productService.createProduct(productDTO);
        }

        return ResponseEntity.ok("Fake data generated!");
    }
     */
}
