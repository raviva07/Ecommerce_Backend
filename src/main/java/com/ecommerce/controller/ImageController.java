/*package com.ecommerce.controller;

import com.ecommerce.constants.ApiMessages;
import com.ecommerce.response.ApiResponse;
import com.ecommerce.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryService.uploadImage(file);
        return ApiResponse.<String>builder()
                .success(true)
                .message(ApiMessages.IMAGE_UPLOADED)
                .data(imageUrl)
                .build();
    }
}*/

