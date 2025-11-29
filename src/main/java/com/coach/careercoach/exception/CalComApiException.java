package com.coach.careercoach.exception;

/**
 * Cal.com API调用异常
 */
public class CalComApiException extends BusinessException {

    public CalComApiException(String message) {
        super("CALCOM_API_ERROR", message);
    }

    public CalComApiException(String message, Throwable cause) {
        super("CALCOM_API_ERROR", message);
        initCause(cause);
    }
}

