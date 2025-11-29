package com.coach.careercoach.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取消预约链接响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelUrlResponse {
    
    private String url;
    private String message;
}

