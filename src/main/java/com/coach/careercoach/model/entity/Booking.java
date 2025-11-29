package com.coach.careercoach.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.coach.careercoach.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 预约实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("cc_booking")
public class Booking {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private String calCoachId;

    private String coachName;

    private String coachEmail;

    private String userName;

    private String userEmail;

    private String externalSlotId;

    private String externalBookingId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String meetingUrl;

    private BookingStatus status;

    @Version
    private Integer version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
