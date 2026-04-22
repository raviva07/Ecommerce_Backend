package com.ecommerce.controller;

import com.ecommerce.constants.ApiMessages;
import com.ecommerce.dto.UserResponse;
import com.ecommerce.dto.UserUpdateRequest;
import com.ecommerce.dto.UserUpdateResponse;
import com.ecommerce.entity.User;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.response.ApiResponse;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile(Authentication authentication) {
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message(ApiMessages.USER_FETCHED)
                .data(UserMapper.toResponse(userService.getUserByEmail(authentication.getName())))
                .build();
    }

    @PutMapping("/profile")
    public ApiResponse<UserUpdateResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest request) {
        User user = userService.getUserByEmail(authentication.getName());
        return ApiResponse.<UserUpdateResponse>builder()
                .success(true)
                .message(ApiMessages.USER_UPDATED)
                .data(UserMapper.toUpdateResponse(userService.updateProfile(user, request)))
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers()
                .stream().map(UserMapper::toResponse).toList();
        return ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .message(ApiMessages.USER_FETCHED)
                .data(users)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.<String>builder()
                .success(true)
                .message(ApiMessages.USER_DELETED)
                .data("User deleted successfully")
                .build();
    }
    
 // ADMIN: Update any user
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUserByAdmin(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {

        User user = userService.updateUserByAdmin(id, request);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User updated successfully")
                .data(UserMapper.toResponse(user))
                .build();
    }

}
