package com.coach.careercoach.external;

import com.coach.careercoach.dto.calcom.CalBookingResponse;
import com.coach.careercoach.dto.calcom.SlotResponse;

import java.time.LocalDate;

/**
 * Cal.com API 客户端接口
 */
public interface CalComClient {

    /**
     * 获取可用时间槽
     * @param userId 用户ID
     * @param eventTypeId Event Type ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 可用时间槽响应
     */
    SlotResponse getAvailableSlots(Long userId, Long eventTypeId, LocalDate startDate, LocalDate endDate);

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

    /**
     * 获取用户的预约列表
     * @param userId 用户ID
     * @param attendeeEmail 参会者邮箱
     * @return Cal.com预约列表响应
     */
    CalBookingResponse getUserBookings(Long userId, String attendeeEmail);
}

