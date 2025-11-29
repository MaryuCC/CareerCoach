package com.coach.careercoach.dto.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Cal.com Webhook 数据结构
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalWebhookPayload {

    @JsonProperty("triggerEvent")
    private String triggerEvent; // BOOKING_CREATED, BOOKING_CANCELLED, etc.

    @JsonProperty("payload")
    private BookingPayload payload;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BookingPayload {
        
        @JsonProperty("id")
        private Long id; // Cal.com booking ID
        
        @JsonProperty("uid")
        private String uid; // Cal.com booking UID
        
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("startTime")
        private String startTime; // ISO 8601 format
        
        @JsonProperty("endTime")
        private String endTime;
        
        @JsonProperty("attendees")
        private java.util.List<Attendee> attendees;
        
        @JsonProperty("organizer")
        private Organizer organizer;
        
        @JsonProperty("metadata")
        private java.util.Map<String, Object> metadata;
        
        @JsonProperty("location")
        private String location; // meeting URL
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attendee {
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("timeZone")
        private String timeZone;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Organizer {
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("username")
        private String username;
    }
}

