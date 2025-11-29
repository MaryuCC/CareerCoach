package com.coach.careercoach.service.impl;

import com.coach.careercoach.mapper.UserMapper;
import com.coach.careercoach.model.entity.User;
import com.coach.careercoach.service.UserService;
import com.coach.careercoach.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Override
    public User getById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public boolean updateApiKeyHash(Long userId, String apiKey) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在: " + userId);
        }
        
        // 加密 API Key 后存储
        String encryptedApiKey = encryptionUtil.encrypt(apiKey);
        user.setApikeyHash(encryptedApiKey);
        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }
}

