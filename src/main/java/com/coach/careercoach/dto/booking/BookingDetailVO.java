package com.coach.careercoach.dto.booking;

import com.coach.careercoach.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约详情视图对象
 */
@Data
public class BookingDetailVO {

    private Long id;
    private String coachName;
    private String coachEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String meetingUrl;
    private BookingStatus status;
    private String externalBookingId;
    private LocalDateTime createdAt;
}

