package com.coach.careercoach.dto.booking;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建预约请求
 */
@Data
public class BookingCreateRequest {

    @NotBlank
    private String calCoachId;

    @NotBlank
    private String externalSlotId;

    private String remark;
}
