package com.coach.careercoach.dto.calcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Cal.com API 预约API响应
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingApiResponse {
    
    private String status;
    private BookingData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BookingData {
        private Long id;
        private String uid; // 预约唯一标识
        private String title;
        
        @JsonProperty("startTime")
        private String startTime; // ISO 8601 format
        
        @JsonProperty("endTime")
        private String endTime;
        
        private String status; // ACCEPTED, PENDING, CANCELLED, etc.
        
        private List<Attendee> attendees;
        
        private Organizer organizer;
        
        private String location; // meeting URL
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attendee {
        private String name;
        private String email;
        private String timeZone;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Organizer {
        private String name;
        private String email;
        private Long id;
    }
}

