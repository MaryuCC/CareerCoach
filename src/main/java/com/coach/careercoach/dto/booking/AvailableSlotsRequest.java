package com.coach.careercoach.dto.booking;

import lombok.Data;

/**
 * 查询可用时间槽请求
 */
@Data
public class AvailableSlotsRequest {
    
    private String startDate; // 格式: 2024-12-01
    private String endDate;   // 格式: 2024-12-31
}

