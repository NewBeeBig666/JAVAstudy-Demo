package com.la.controller;

import com.la.dto.ApiResponse;
import com.la.dto.request.LoginRequest;
import com.la.dto.request.RegisterRequest;
import com.la.dto.response.AuthResponse;
import com.la.security.CurrentUser;
import com.la.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "注册")
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ApiResponse.ok(authService.register(req));
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ApiResponse.ok(authService.login(req));
    }

    @Operation(summary = "当前用户信息")
    @GetMapping("/me")
    public ApiResponse<AuthResponse> me() {
        return ApiResponse.ok(authService.me(CurrentUser.id()));
    }
}
