package com.coach.careercoach.dto.calcom;

import lombok.Builder;
import lombok.Data;

/**
 * Cal.com API 取消预约请求
 */
@Data
@Builder
public class CancelBookingRequest {
    
    private String cancellationReason;
}

