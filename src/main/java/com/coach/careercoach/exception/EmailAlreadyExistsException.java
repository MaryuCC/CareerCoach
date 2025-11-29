package com.coach.careercoach.exception;

/**
 * 邮箱已存在异常
 */
public class EmailAlreadyExistsException extends BusinessException {

    public EmailAlreadyExistsException(String email) {
        super("EMAIL_EXISTS", "邮箱已被注册: " + email);
    }
}

