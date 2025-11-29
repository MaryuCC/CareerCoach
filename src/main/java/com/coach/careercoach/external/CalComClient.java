package com.coach.careercoach.external;

/**
 * Cal.com API 客户端接口
 */
public interface CalComClient {

    /**
     * 获取预约链接
     * @param userId 用户ID
     * @return 预约链接URL
     */
    String getBookingUrl(Long userId);

    /**
     * 获取取消预约链接
     * @param externalBookingId Cal.com预约ID
     * @return 取消链接URL
     */
    String getCancelUrl(String externalBookingId);
}

