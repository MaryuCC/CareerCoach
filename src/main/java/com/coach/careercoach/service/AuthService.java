package com.coach.careercoach.service;

import com.coach.careercoach.dto.auth.LoginRequest;
import com.coach.careercoach.dto.auth.LoginResponse;
import com.coach.careercoach.dto.auth.RegisterRequest;
import com.coach.careercoach.dto.auth.RegisterResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     */
    RegisterResponse register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
}
