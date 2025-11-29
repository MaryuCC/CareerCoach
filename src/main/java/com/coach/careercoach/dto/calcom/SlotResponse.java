package com.coach.careercoach.dto.calcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Cal.com API 可用时间槽响应
 * 响应格式: { "status": "success", "data": { "2025-12-01": [{"start": "..."}], ... } }
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlotResponse {
    
    private String status;

    private Map<String, List<Map<String, String>>> data;
}

