package com.ecommerce.controller;

import com.ecommerce.constants.ApiMessages;
import com.ecommerce.dto.ImageUrlRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.response.ApiResponse;
import com.ecommerce.service.CloudinaryService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    // ================================
    // ✅ FIXED: UPLOAD IMAGE URL → CLOUDINARY
    // ================================
    @PostMapping("/upload-url")
    public ApiResponse<String> uploadImageFromUrl(@RequestBody ImageUrlRequest request) {

        if (request.getImageUrl() == null || request.getImageUrl().isBlank()) {
            throw new IllegalArgumentException("Image URL cannot be empty");
        }

        // ✅ Upload to Cloudinary instead of returning same URL
        String cloudinaryUrl = cloudinaryService.uploadImageFromUrl(request.getImageUrl());

        return ApiResponse.<String>builder()
                .success(true)
                .message("Image uploaded to Cloudinary")
                .data(cloudinaryUrl)
                .build();
    }

    // ================================
    // CREATE PRODUCT (MULTIPART)
    // ================================
    @PostMapping(consumes = {"multipart/form-data"})
    public ApiResponse<ProductResponse> addProductMultipart(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Integer stock,
            @RequestParam String category,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            Authentication authentication
    ) {
        User admin = userService.getUserByEmail(authentication.getName());

        String finalImageUrl = null;
        List<String> extraImages = new ArrayList<>();

        // ✅ FILE UPLOAD
        if (file != null && !file.isEmpty()) {
            finalImageUrl = cloudinaryService.uploadImage(file);
        }

        // ✅ URL → CLOUDINARY
        else if (imageUrl != null && !imageUrl.isBlank()) {
            finalImageUrl = cloudinaryService.uploadImageFromUrl(imageUrl);
        }

        // ✅ MULTIPLE FILES
        if (files != null && !files.isEmpty()) {
            for (MultipartFile f : files) {
                if (!f.isEmpty()) {
                    extraImages.add(cloudinaryService.uploadImage(f));
                }
            }
        }

        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(BigDecimal.valueOf(price))
                .stock(stock)
                .category(category)
                .imageUrl(finalImageUrl)
                .images(extraImages)
                .isActive(true)
                .build();

        ProductResponse response = ProductMapper.toResponse(
                productService.addProduct(product, admin)
        );

        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message(ApiMessages.PRODUCT_CREATED)
                .data(response)
                .build();
    }

    // ================================
    // CREATE PRODUCT (JSON)
    // ================================
    @PostMapping(consumes = {"application/json"})
    public ApiResponse<ProductResponse> addProductJson(
            @RequestBody Product product,
            Authentication authentication) {

        User admin = userService.getUserByEmail(authentication.getName());

        // ✅ If image URL exists → upload to Cloudinary
        if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
            String cloudinaryUrl =
                    cloudinaryService.uploadImageFromUrl(product.getImageUrl());
            product.setImageUrl(cloudinaryUrl);
        }

        ProductResponse response = ProductMapper.toResponse(
                productService.addProduct(product, admin)
        );

        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message(ApiMessages.PRODUCT_CREATED)
                .data(response)
                .build();
    }

    // ================================
    // UPDATE PRODUCT
    // ================================
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        // ✅ If updating image URL → upload to Cloudinary
        if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
            String cloudinaryUrl =
                    cloudinaryService.uploadImageFromUrl(product.getImageUrl());
            product.setImageUrl(cloudinaryUrl);
        }

        ProductResponse response = ProductMapper.toResponse(
                productService.updateProduct(id, product)
        );

        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message(ApiMessages.PRODUCT_UPDATED)
                .data(response)
                .build();
    }

    // ================================
    // DELETE PRODUCT
    // ================================
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        return ApiResponse.<String>builder()
                .success(true)
                .message(ApiMessages.PRODUCT_DELETED)
                .data("Product marked inactive")
                .build();
    }

    // ================================
    // GET ALL PRODUCTS
    // ================================
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {

        List<ProductResponse> products = productService.getAllProducts()
                .stream()
                .map(ProductMapper::toResponse)
                .toList();

        return ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .message(ApiMessages.PRODUCT_LIST)
                .data(products)
                .build();
    }

    // ================================
    // GET PRODUCT BY ID
    // ================================
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {

        Product product = productService.getProductById(id);

        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message(ApiMessages.PRODUCT_FETCHED)
                .data(ProductMapper.toResponse(product))
                .build();
    }

    // ================================
    // SEARCH PRODUCTS
    // ================================
    @GetMapping("/search")
    public ApiResponse<List<ProductResponse>> searchProducts(
            @RequestParam String keyword) {

        List<ProductResponse> products = productService.searchProducts(keyword)
                .stream()
                .map(ProductMapper::toResponse)
                .toList();

        return ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .message(ApiMessages.PRODUCT_SEARCH)
                .data(products)
                .build();
    }
}
