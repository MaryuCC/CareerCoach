package com.coach.careercoach.controller;

import com.coach.careercoach.api.ApiResponse;
import com.coach.careercoach.dto.auth.LoginRequest;
import com.coach.careercoach.dto.auth.LoginResponse;
import com.coach.careercoach.dto.auth.RegisterRequest;
import com.coach.careercoach.dto.auth.RegisterResponse;
import com.coach.careercoach.service.AuthService;
import com.coach.careercoach.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 * 处理用户认证和管理功能
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ApiResponse.ok(response);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.ok(response);
    }

    /**
     * 更新用户的API Key
     */
    @PostMapping("/{userId}/apikey")
    public ApiResponse<String> updateApiKey(@PathVariable Long userId,
                                           @RequestBody Map<String, String> request) {
        String apiKey = request.get("apiKey");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return ApiResponse.fail("API Key不能为空");
        }
        boolean success = userService.updateApiKeyHash(userId, apiKey);
        return success ? ApiResponse.ok("API Key更新成功") : ApiResponse.fail("API Key更新失败");
    }
}
