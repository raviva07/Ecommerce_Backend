package com.ecommerce.dto;

import com.ecommerce.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    private Long userId;
    private String name;
    private String email;
    private Role role;
    private String message;
}

