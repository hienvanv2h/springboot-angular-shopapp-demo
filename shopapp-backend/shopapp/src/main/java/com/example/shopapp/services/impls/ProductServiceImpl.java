package com.example.shopapp.services.impls;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.ProductDTO;
import com.example.shopapp.dtos.ProductImageDTO;
import com.example.shopapp.exceptionhandling.DataNotFoundException;
import com.example.shopapp.models.Category;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;
import com.example.shopapp.repositories.CategoryRepository;
import com.example.shopapp.repositories.ProductImageRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.responses.GenericResponse;
import com.example.shopapp.responses.ProductResponse;
import com.example.shopapp.services.ProductService;
import com.example.shopapp.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ProductImageRepository productImageRepository;

    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils
                                        .getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, productDTO.getCategoryId()))
                );
        String thumbnailUrl = productDTO.getThumbnailUrl() != null ? productDTO.getThumbnailUrl() : "";
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnailUrl(thumbnailUrl)
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();

        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, id)));
    }

    @Override
    public List<Product> getProductsByIds(List<Long> productIds) {
        return productRepository.findByProductIds(productIds);
    }

    @Override
    public List<ProductImage> getAllProductImages(Long productId) {
        return productImageRepository.findByProductId(productId);
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        // Map product to ProductResponse and return
        return productRepository.searchProducts(keyword, categoryId, pageRequest)
                .map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = getProductById(id);
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, productDTO.getCategoryId()))
                );

        existingProduct.setName(productDTO.getName());
        existingProduct.setCategory(existingCategory);
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setThumbnailUrl(productDTO.getThumbnailUrl());
        existingProduct.setDescription(productDTO.getDescription());

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        // hard delete
        if(optionalProduct.isPresent()) {
            productRepository.delete(optionalProduct.get());
        } else {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, id)
            );
        }
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    public ResponseEntity<?> validateAndStoreFiles(
            Long productId,
            List<MultipartFile> files
    ) throws Exception {
        // Check if file uploaded, if not then skip this process
        files = files == null ? new ArrayList<>() : files;
        // Limit maximum images per product (check number of images that existed)
        int numberOfImages = productImageRepository.findByProductId(productId).size();
        if(numberOfImages + files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            return ResponseEntity.badRequest().body(
                    GenericResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(
                                            MessageKeys.PRODUCT_IMAGE_MAXIMUM_QUANTITY,
                                            ProductImage.MAXIMUM_IMAGES_PER_PRODUCT,
                                            numberOfImages)
                            )
                            .build()
            );
        }

        List<ProductImage> productImages = new ArrayList<>();
        for(var file : files) {
            // Check file size
            if(file.getSize() == 0) {   // empty size
                continue;
            }
            if(file.getSize() > 10 * 1024 * 1024) {     // size > 10MB
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                        GenericResponse.builder()
                                        .message(localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_IMAGE_MAXIMUM_SIZE))
                                        .build()
                        );
            }
            // Check content type
            String contentType = file.getContentType();
            if(contentType == null || !contentType.startsWith("image/")) {
                return  ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                        GenericResponse.builder()
                                .message(localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_IMAGE_INVALID_FILETYPE))
                                .build()
                );
            }
            String filename = storeFile(file);
            // save product image in db
            ProductImageDTO productImageDTO = ProductImageDTO.builder()
                    .productId(productId)
                    .imageUrl(filename)
                    .build();
            ProductImage productImage = createProductImage(productImageDTO);
            productImages.add(productImage);
        }
        return ResponseEntity.ok(
                GenericResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_IMAGE_UPLOAD_SUCCESS))
                        .build()
        );
    }

    // PRIVATE METHODS
    private ProductImage createProductImage(ProductImageDTO productImageDTO) {
        Product existingProduct = getProductById(productImageDTO.getProductId());

        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        return productImageRepository.save(newProductImage);
    }

    private String storeFile(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueName = UUID.randomUUID().toString() + "_" + filename;

        Path uploadDir = Paths.get("uploads");
        if(!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path destination = Paths.get(uploadDir.toString(), uniqueName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        // return file name
        return uniqueName;
    }
}
