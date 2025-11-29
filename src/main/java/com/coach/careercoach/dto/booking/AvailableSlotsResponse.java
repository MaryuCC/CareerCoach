package com.coach.careercoach.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 可用时间槽响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotsResponse {
    
    private Long eventTypeId;
    private String eventTypeName;
    
    // Map<日期, List<时间槽>>
    // 例如: {"2024-12-01": ["10:00", "11:00", "14:00"]}
    private Map<String, List<String>> availableSlots;
    
    private Integer totalSlots;
}

