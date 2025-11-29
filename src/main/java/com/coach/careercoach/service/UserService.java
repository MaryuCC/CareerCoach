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
     * 更新用户的API Key
     * @param userId 用户ID
     * @param apiKeyHash API Key的哈希值
     * @return 是否更新成功
     */
    boolean updateApiKeyHash(Long userId, String apiKeyHash);
}

