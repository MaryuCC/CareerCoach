package com.coach.careercoach.dto.calcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Cal.com API 可用时间槽响应
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlotResponse {
    
    private String status;
    private SlotData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SlotData {
        // slots是一个Map，key是日期字符串，value是时间槽数组
        // 例如: {"2024-12-01": ["2024-12-01T10:00:00Z", "2024-12-01T11:00:00Z"]}
        private Map<String, List<String>> slots;
    }
}

