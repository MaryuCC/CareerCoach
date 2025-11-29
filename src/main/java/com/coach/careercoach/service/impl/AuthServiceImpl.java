package com.coach.careercoach.service.impl;

import com.coach.careercoach.dto.auth.LoginRequest;
import com.coach.careercoach.dto.auth.LoginResponse;
import com.coach.careercoach.dto.auth.RegisterRequest;
import com.coach.careercoach.dto.auth.RegisterResponse;
import com.coach.careercoach.dto.user.UserInfoVO;
import com.coach.careercoach.exception.EmailAlreadyExistsException;
import com.coach.careercoach.exception.InvalidPasswordException;
import com.coach.careercoach.exception.UserNotFoundException;
import com.coach.careercoach.mapper.UserMapper;
import com.coach.careercoach.model.entity.User;
import com.coach.careercoach.service.AuthService;
import com.coach.careercoach.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 1. 验证邮箱是否已存在
        User existingUser = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getEmail, request.getEmail())
        );
        
        if (existingUser != null) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // 2. 使用BCrypt加密密码
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 创建User实体并保存到数据库
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userMapper.insert(user);

        // 4. 返回用户基本信息
        return RegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .message("注册成功")
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 根据邮箱查询用户
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getEmail, request.getEmail())
        );

        // 2. 验证用户是否存在
        if (user == null) {
            throw new UserNotFoundException(request.getEmail());
        }

        // 3. 使用BCrypt验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException();
        }

        // 4. 生成JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // 5. 返回token和用户信息
        UserInfoVO userInfo = UserInfoVO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();

        return LoginResponse.builder()
                .token(token)
                .userInfo(userInfo)
                .build();
    }
}
