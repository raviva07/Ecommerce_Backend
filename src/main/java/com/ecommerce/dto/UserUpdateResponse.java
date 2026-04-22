package com.ecommerce.dto;

import com.ecommerce.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String message;
}

