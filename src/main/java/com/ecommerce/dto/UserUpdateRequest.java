package com.ecommerce.dto;

import com.ecommerce.entity.Role;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;
 
    private Role role;
}
