package com.coach.careercoach.enums;

/**
 * 预约状态枚举
 */
public enum BookingStatus {
    /** 初始状态 */
    PENDING,
    
    /** 预约已创建（支付成功且确认） */
    BOOKING_CREATED,
    
    /** 预约已取消 */
    BOOKING_CANCELLED,
    
    /** 会议正常结束 */
    MEETING_ENDED,
    
    /** 未出席 */
    NO_SHOW
}
