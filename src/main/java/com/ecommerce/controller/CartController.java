package com.ecommerce.controller;

import com.ecommerce.constants.ApiMessages;
import com.ecommerce.dto.CartResponse;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.User;
import com.ecommerce.mapper.CartMapper;
import com.ecommerce.response.ApiResponse;
import com.ecommerce.service.CartService;
import com.ecommerce.service.UserService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public ApiResponse<CartResponse> getCart(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ApiResponse.<CartResponse>builder()
                .success(true)
                .message(ApiMessages.CART_FETCHED)
                .data(CartMapper.toResponse(cartService.getOrCreateCart(user)))
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<CartResponse> addToCart(
        @RequestParam Long productId,
        @RequestParam int quantity,
        Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ApiResponse.<CartResponse>builder()
            .success(true)
            .message("Item added to cart")
            .data(CartMapper.toResponse(cartService.addToCart(user, productId, quantity)))
            .build();
    }


    @PutMapping("/update")
    public ApiResponse<CartResponse> updateQuantity(Authentication authentication,
                                                    @RequestParam @NotNull Long productId,
                                                    @RequestParam @Min(1) int quantity) {
        User user = userService.getUserByEmail(authentication.getName());
        return ApiResponse.<CartResponse>builder()
                .success(true)
                .message(ApiMessages.CART_ITEM_UPDATED)
                .data(CartMapper.toResponse(cartService.updateQuantity(user, productId, quantity)))
                .build();
    }

    @DeleteMapping("/remove")
    public ApiResponse<CartResponse> removeItem(Authentication authentication,
                                                @RequestParam @NotNull Long productId) {
        User user = userService.getUserByEmail(authentication.getName());
        Cart updatedCart = cartService.removeItem(user, productId);
        return ApiResponse.<CartResponse>builder()
                .success(true)
                .message(ApiMessages.CART_ITEM_REMOVED)
                .data(CartMapper.toResponse(updatedCart))
                .build();
    }

    @DeleteMapping("/clear")
    public ApiResponse<CartResponse> clearCart(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        Cart clearedCart = cartService.clearCart(user);
        return ApiResponse.<CartResponse>builder()
                .success(true)
                .message(ApiMessages.CART_CLEARED)
                .data(CartMapper.toResponse(clearedCart))
                .build();
    }
}

