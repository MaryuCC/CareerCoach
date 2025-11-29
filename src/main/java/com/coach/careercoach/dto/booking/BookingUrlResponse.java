package com.coach.careercoach.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取预约链接响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingUrlResponse {
    
    private String url;
}

