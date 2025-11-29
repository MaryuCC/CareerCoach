package com.coach.careercoach.external;

import com.coach.careercoach.dto.calcom.EventTypeResponse;
import com.coach.careercoach.dto.calcom.SlotResponse;

import java.time.LocalDate;

/**
 * Cal.com API 客户端接口
 */
public interface CalComClient {

    /**
     * 获取所有Event Types
     * @return Event Types响应
     */
    EventTypeResponse getEventTypes();

    /**
     * 获取可用时间槽
     * @param eventTypeId Event Type ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 可用时间槽响应
     */
    SlotResponse getAvailableSlots(Long eventTypeId, LocalDate startDate, LocalDate endDate);

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

