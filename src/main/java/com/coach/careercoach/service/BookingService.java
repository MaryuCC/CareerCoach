package com.coach.careercoach.service;

import com.coach.careercoach.dto.booking.AvailableSlotsResponse;
import com.coach.careercoach.dto.booking.BookingDetailVO;
import com.coach.careercoach.dto.webhook.CalWebhookPayload;
import com.coach.careercoach.model.entity.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    /**
     * 获取可用时间槽（预约前的第一步）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 可用时间槽
     */
    AvailableSlotsResponse getAvailableSlots(LocalDate startDate, LocalDate endDate);

    /**
     * 获取预约链接（预约的最后一步）
     * @param userId 用户ID
     * @return 预约链接URL
     */
    String getBookingUrl(Long userId);

    /**
     * 查询用户的预约列表
     * @param userId 用户ID
     * @return 预约详情列表
     */
    List<BookingDetailVO> listUserBookings(Long userId);

    /**
     * 获取取消预约链接
     * @param bookingId 预约ID
     * @return 取消链接URL
     */
    String getCancelUrl(Long bookingId);

    /**
     * 处理Cal.com Webhook事件
     * @param payload Webhook数据
     */
    void handleWebhook(CalWebhookPayload payload);

    /**
     * 根据ID查询预约
     * @param bookingId 预约ID
     * @return 预约实体
     */
    Booking getById(Long bookingId);
}
