package com.coach.careercoach.exception;

/**
 * 用户不存在异常
 */
public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String email) {
        super("USER_NOT_FOUND", "用户不存在: " + email);
    }
}

