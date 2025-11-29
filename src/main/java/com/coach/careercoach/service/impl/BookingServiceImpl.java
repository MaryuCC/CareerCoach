package com.coach.careercoach.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coach.careercoach.dto.booking.BookingDetailVO;
import com.coach.careercoach.dto.webhook.CalWebhookPayload;
import com.coach.careercoach.enums.BookingStatus;
import com.coach.careercoach.external.CalComClient;
import com.coach.careercoach.mapper.BookingMapper;
import com.coach.careercoach.mapper.UserMapper;
import com.coach.careercoach.model.entity.Booking;
import com.coach.careercoach.model.entity.User;
import com.coach.careercoach.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CalComClient calComClient;

    @Override
    public String getBookingUrl(Long userId) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在: " + userId);
        }

        // 调用Cal.com客户端获取预约链接
        return calComClient.getBookingUrl(userId);
    }

    @Override
    public List<BookingDetailVO> listUserBookings(Long userId) {
        // 查询该用户的所有预约
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Booking::getStudentId, userId)
               .orderByDesc(Booking::getCreatedAt);

        List<Booking> bookings = bookingMapper.selectList(wrapper);

        // 转换为VO
        return bookings.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public String getCancelUrl(Long bookingId) {
        Booking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约不存在: " + bookingId);
        }

        if (booking.getStatus() == BookingStatus.BOOKING_CANCELLED) {
            throw new RuntimeException("预约已取消");
        }

        if (booking.getStatus() == BookingStatus.MEETING_ENDED) {
            throw new RuntimeException("会议已结束，无法取消");
        }

        // 调用Cal.com客户端获取取消链接
        return calComClient.getCancelUrl(booking.getExternalBookingId());
    }

    @Override
    @Transactional
    public void handleWebhook(CalWebhookPayload payload) {
        String triggerEvent = payload.getTriggerEvent();
        CalWebhookPayload.BookingPayload bookingData = payload.getPayload();

        if (bookingData == null) {
            throw new RuntimeException("Webhook payload为空");
        }

        switch (triggerEvent) {
            case "BOOKING_CREATED":
                handleBookingCreated(bookingData);
                break;
            case "BOOKING_CANCELLED":
                handleBookingCancelled(bookingData);
                break;
            case "BOOKING_RESCHEDULED":
                handleBookingRescheduled(bookingData);
                break;
            default:
                // 忽略其他事件
                break;
        }
    }

    @Override
    public Booking getById(Long bookingId) {
        return bookingMapper.selectById(bookingId);
    }

    private void handleBookingCreated(CalWebhookPayload.BookingPayload bookingData) {
        // 从webhook数据创建预约记录
        Booking booking = new Booking();

        // 获取参会者信息（用户）
        if (bookingData.getAttendees() != null && !bookingData.getAttendees().isEmpty()) {
            CalWebhookPayload.Attendee attendee = bookingData.getAttendees().get(0);
            booking.setUserName(attendee.getName());
            booking.setUserEmail(attendee.getEmail());

            // 尝试根据邮箱查找用户ID
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(User::getEmail, attendee.getEmail());
            User user = userMapper.selectOne(userWrapper);
            if (user != null) {
                booking.setStudentId(user.getId());
            }
        }

        // 获取组织者信息（教练）
        if (bookingData.getOrganizer() != null) {
            booking.setCoachName(bookingData.getOrganizer().getName());
            booking.setCoachEmail(bookingData.getOrganizer().getEmail());
            booking.setCalCoachId(bookingData.getOrganizer().getUsername());
        }

        // 设置预约信息
        booking.setExternalBookingId(bookingData.getUid());
        booking.setStartTime(parseDateTime(bookingData.getStartTime()));
        booking.setEndTime(parseDateTime(bookingData.getEndTime()));
        booking.setMeetingUrl(bookingData.getLocation());
        booking.setStatus(BookingStatus.BOOKING_CREATED);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        bookingMapper.insert(booking);
    }

    private void handleBookingCancelled(CalWebhookPayload.BookingPayload bookingData) {
        // 根据externalBookingId查找预约
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Booking::getExternalBookingId, bookingData.getUid());
        Booking booking = bookingMapper.selectOne(wrapper);

        if (booking != null) {
            booking.setStatus(BookingStatus.BOOKING_CANCELLED);
            booking.setUpdatedAt(LocalDateTime.now());
            bookingMapper.updateById(booking);
        }
    }

    private void handleBookingRescheduled(CalWebhookPayload.BookingPayload bookingData) {
        // 重新安排预约时间
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Booking::getExternalBookingId, bookingData.getUid());
        Booking booking = bookingMapper.selectOne(wrapper);

        if (booking != null) {
            booking.setStartTime(parseDateTime(bookingData.getStartTime()));
            booking.setEndTime(parseDateTime(bookingData.getEndTime()));
            booking.setMeetingUrl(bookingData.getLocation());
            booking.setUpdatedAt(LocalDateTime.now());
            bookingMapper.updateById(booking);
        }
    }

    private BookingDetailVO convertToVO(Booking booking) {
        BookingDetailVO vo = new BookingDetailVO();
        vo.setId(booking.getId());
        vo.setCoachName(booking.getCoachName());
        vo.setCoachEmail(booking.getCoachEmail());
        vo.setStartTime(booking.getStartTime());
        vo.setEndTime(booking.getEndTime());
        vo.setMeetingUrl(booking.getMeetingUrl());
        vo.setStatus(booking.getStatus());
        vo.setExternalBookingId(booking.getExternalBookingId());
        vo.setCreatedAt(booking.getCreatedAt());
        return vo;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        // ISO 8601格式: 2024-01-15T10:00:00Z 或 2024-01-15T10:00:00+08:00
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            // 如果解析失败，尝试其他格式或返回当前时间
            return LocalDateTime.now();
        }
    }
}
