package com.coach.careercoach.dto.calcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Cal.com API Event Types响应
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventTypeResponse {
    
    private String status;
    private List<EventType> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventType {
        private Long id;
        private String title;
        private String slug;
        private Integer length; // 时长（分钟）
        private String description;
        
        @JsonProperty("schedulingType")
        private String schedulingType; // ROUND_ROBIN, COLLECTIVE, etc.
        
        private List<Host> hosts;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Host {
        private Long id;
        private String name;
        private String email;
    }
}

