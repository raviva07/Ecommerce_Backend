package com.ecommerce.mapper;

import com.ecommerce.dto.*;
import com.ecommerce.entity.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public static RegisterResponse toRegisterResponse(User user) {
        return RegisterResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .message("User registered successfully")
                .build();
    }

    public static UserUpdateResponse toUpdateResponse(User user) {
        return UserUpdateResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Profile updated successfully")
                .build();
    }
}

