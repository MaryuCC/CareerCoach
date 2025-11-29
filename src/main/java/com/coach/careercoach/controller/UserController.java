package com.coach.careercoach.controller;

import com.coach.careercoach.api.ApiResponse;
import com.coach.careercoach.dto.auth.LoginRequest;
import com.coach.careercoach.dto.auth.LoginResponse;
import com.coach.careercoach.dto.auth.RegisterRequest;
import com.coach.careercoach.dto.auth.RegisterResponse;
import com.coach.careercoach.dto.coach.CoachQueryRequest;
import com.coach.careercoach.model.entity.CoachProfile;
import com.coach.careercoach.service.AuthService;
import com.coach.careercoach.service.CoachService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户相关控制器
 * 包括认证、教练查询等功能
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AuthService authService;
    private final CoachService coachService;

    public UserController(AuthService authService, CoachService coachService) {
        this.authService = authService;
        this.coachService = coachService;
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
     * 查询教练列表
     */
    @GetMapping("/coaches")
    public ApiResponse<List<CoachProfile>> listCoaches(@ModelAttribute CoachQueryRequest query,
                                                       @RequestParam(defaultValue = "1") long page,
                                                       @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(coachService.list(query, page, size));
    }

    /**
     * 查询教练详情
     */
    @GetMapping("/coaches/{calCoachId}")
    public ApiResponse<CoachProfile> coachDetail(@PathVariable String calCoachId) {
        return ApiResponse.ok(coachService.findByCalCoachId(calCoachId));
    }
}
