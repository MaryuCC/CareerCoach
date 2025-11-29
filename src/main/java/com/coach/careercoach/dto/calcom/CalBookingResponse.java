package com.coach.careercoach.dto.calcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Cal.com API 预约列表响应
 * GET /v2/bookings 的返回格式
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalBookingResponse {
    
    private String status;
    
    private List<BookingData> data;
    
    private Pagination pagination;
    
    /**
     * 单个预约数据
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BookingData {
        
        private Long id;
        
        private String uid;
        
        private String title;
        
        private String description;
        
        private List<Host> hosts;
        
        private String status;  // accepted, cancelled, pending, etc.
        
        private String cancellationReason;
        
        @JsonProperty("start")
        private String startTime;
        
        @JsonProperty("end")
        private String endTime;
        
        private Integer duration;
        
        private Long eventTypeId;
        
        private String meetingUrl;
        
        private String location;
        
        private String createdAt;
        
        private String updatedAt;
        
        private List<Attendee> attendees;
    }
    
    /**
     * 教练/主持人信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Host {
        private Long id;
        private String name;
        private String email;
        private String username;
        private String timeZone;
    }
    
    /**
     * 参会者信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attendee {
        private String name;
        private String email;
        private String timeZone;
        private String language;
        private Boolean absent;
    }
    
    /**
     * 分页信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pagination {
        private Integer totalItems;
        private Integer remainingItems;
        private Integer returnedItems;
        private Integer itemsPerPage;
        private Integer currentPage;
        private Integer totalPages;
        private Boolean hasNextPage;
        private Boolean hasPreviousPage;
    }
}

