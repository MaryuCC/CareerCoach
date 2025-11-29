package com.coach.careercoach.exception;

/**
 * 密码错误异常
 */
public class InvalidPasswordException extends BusinessException {

    public InvalidPasswordException() {
        super("INVALID_PASSWORD", "密码错误");
    }
}

