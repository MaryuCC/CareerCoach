package com.coach.careercoach.service;

import com.coach.careercoach.model.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据ID查询用户
     * @param userId 用户ID
     * @return 用户实体
     */
    User getById(Long userId);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户实体
     */
    User getByEmail(String email);

    /**
     * 创建用户
     * @param user 用户实体
     * @return 创建的用户
     */
    User create(User user);
}

