package com.coach.careercoach.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.coach.careercoach.dto.booking.AvailableSlotsResponse;
import com.coach.careercoach.dto.booking.BookingDetailVO;
import com.coach.careercoach.dto.calcom.CalBookingResponse;
import com.coach.careercoach.dto.calcom.SlotResponse;
import com.coach.careercoach.dto.webhook.CalWebhookPayload;
import com.coach.careercoach.enums.BookingStatus;
import com.coach.careercoach.external.CalComClient;
import com.coach.careercoach.mapper.BookingMapper;
import com.coach.careercoach.mapper.UserMapper;
import com.coach.careercoach.model.entity.Booking;
import com.coach.careercoach.model.entity.User;
import com.coach.careercoach.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CalComClient calComClient;

    @Value("${cal.event-type-id}")
    private Long eventTypeId;

    @Override
    public AvailableSlotsResponse getAvailableSlots(Long userId, LocalDate startDate, LocalDate endDate) {
        // 1. 直接使用配置的eventTypeId调用API，使用用户的API Key
        SlotResponse slotResponse = calComClient.getAvailableSlots(
                userId,       // 用户ID，用于获取API Key
                eventTypeId,  // 从配置读取
                startDate, 
                endDate
        );
        
        // 2. 格式化返回数据
        Map<String, List<String>> slots = new HashMap<>();
        int totalSlots = 0;

        if (slotResponse != null && slotResponse.getData() != null) {
            // 遍历data，提取每个时间槽的start时间
            for (Map.Entry<String, List<Map<String, String>>> entry : slotResponse.getData().entrySet()) {
                String date = entry.getKey();
                List<String> times = entry.getValue().stream()
                        .map(slot -> formatTimeSlot(slot.get("start")))
                        .collect(Collectors.toList());
                slots.put(date, times);
                totalSlots += times.size();
            }
        }

        return AvailableSlotsResponse.builder()
                .eventTypeId(eventTypeId)
                .eventTypeName("Career Coach Session")
                .availableSlots(slots)
                .totalSlots(totalSlots)
                .build();
    }

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
        // 1. 根据userId获取用户邮箱
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在: " + userId);
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new RuntimeException("用户邮箱不存在");
        }

        // 2. 调用Cal.com API获取该用户的预约列表
        CalBookingResponse calResponse = calComClient.getUserBookings(userId, user.getEmail());

        if (calResponse == null || calResponse.getData() == null) {
            return Collections.emptyList();
        }

        // 3. 将Cal.com返回的数据转换为VO
        return calResponse.getData().stream()
                .map(this::convertCalBookingToVO)
                .collect(Collectors.toList());
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
        CalWebhookPayload.BookingPayload bookingData = payload.getPayload();
        log.info("handleWebhook: {}", bookingData);

        if (bookingData == null) {
            throw new RuntimeException("Webhook payload为空");
        }

        handleBookingCreated(bookingData);
    }

    @Override
    @Transactional
    public void handleCancelWebhook(CalWebhookPayload payload) {
        CalWebhookPayload.BookingPayload bookingData = payload.getPayload();
        log.info("handleCancelWebhook: {}", bookingData);

        if (bookingData == null) {
            throw new RuntimeException("Webhook payload为空");
        }

        handleBookingCancelled(bookingData);
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

        // 获取组织者信息（导师）
        if (bookingData.getOrganizer() != null) {
            booking.setCoachName(bookingData.getOrganizer().getName());
            booking.setCoachEmail(bookingData.getOrganizer().getEmail());
        }

        // 提取视频会议URL
        String meetingUrl = bookingData.getLocation();
        if (bookingData.getMetadata() != null && bookingData.getMetadata().containsKey("videoCallUrl")) {
            meetingUrl = (String) bookingData.getMetadata().get("videoCallUrl");
        }

        // 设置预约信息
        booking.setExternalBookingId(bookingData.getUid());
        booking.setStartTime(parseDateTime(bookingData.getStartTime()));
        booking.setEndTime(parseDateTime(bookingData.getEndTime()));
        booking.setMeetingUrl(meetingUrl);
        booking.setStatus(BookingStatus.BOOKING_CREATED);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        bookingMapper.insert(booking);

        // 记录日志
        log.info("用户 {} 预约了导师 {}，支付成功 - 预约时间: {} - {}, 会议id {} ，会议链接: {}", 
            booking.getUserName(), booking.getCoachName(),
            booking.getStartTime(), booking.getEndTime(), booking.getId(), meetingUrl);
    }

    private void handleBookingCancelled(CalWebhookPayload.BookingPayload bookingData) {
        // 根据externalBookingId查找预约
        LambdaQueryWrapper<Booking> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Booking::getExternalBookingId, bookingData.getUid());
        Booking booking = bookingMapper.selectOne(queryWrapper);

        if (booking != null) {
            // 使用 UpdateWrapper 来更新，避免乐观锁问题
            LambdaUpdateWrapper<Booking> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Booking::getExternalBookingId, bookingData.getUid())
                .set(Booking::getStatus, BookingStatus.BOOKING_CANCELLED)
                .set(Booking::getUpdatedAt, LocalDateTime.now());
            
            bookingMapper.update(null, updateWrapper);
            
            log.info("用户 {} 与导师 {} 的预约已取消 - 预约ID: {}", 
                booking.getUserName(), booking.getCoachName(), booking.getId());
        } else {
            log.warn("未找到对应的预约记录，externalBookingId: {}", bookingData.getUid());
        }
    }

    /**
     * 将Cal.com的预约数据转换为VO
     */
    private BookingDetailVO convertCalBookingToVO(CalBookingResponse.BookingData calBooking) {
        BookingDetailVO vo = new BookingDetailVO();
        
        // 1. 预约状态
        vo.setStatus(calBooking.getStatus());
        
        // 2. Coach名称
        if (calBooking.getHosts() != null && !calBooking.getHosts().isEmpty()) {
            CalBookingResponse.Host host = calBooking.getHosts().get(0);
            vo.setCoachName(host.getName());
        }
        
        // 3. 预约时间段
        vo.setStartTime(calBooking.getStartTime());
        vo.setEndTime(calBooking.getEndTime());
        
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

    /**
     * 格式化时间槽（从ISO 8601格式提取时间部分）
     */
    private String formatTimeSlot(String isoDateTime) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_DATE_TIME);
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return isoDateTime; // 如果解析失败，返回原始值
        }
    }
}
