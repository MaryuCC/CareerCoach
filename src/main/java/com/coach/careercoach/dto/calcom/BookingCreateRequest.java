package com.coach.careercoach.dto.calcom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Cal.com API 创建预约请求
 */
@Data
@Builder
public class BookingCreateRequest {
    
    @JsonProperty("eventTypeId")
    private Long eventTypeId;
    
    private String start; // ISO 8601 format
    
    private List<Attendee> attendee;
    
    private String timeZone;
    
    private String language;
    
    private Object metadata;

    @Data
    @Builder
    public static class Attendee {
        private String name;
        private String email;
        private String timeZone;
        private String language;
    }
}

