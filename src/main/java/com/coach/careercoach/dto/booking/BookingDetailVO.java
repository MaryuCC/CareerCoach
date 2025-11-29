package com.coach.careercoach.dto.booking;

import lombok.Data;

/**
 * 预约详情视图对象
 */
@Data
public class BookingDetailVO {

    /** 预约状态 */
    private String status;
    
    /** Coach名称 */
    private String coachName;
    
    /** 预约开始时间 */
    private String startTime;
    
    /** 预约结束时间 */
    private String endTime;
}

