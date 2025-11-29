package com.coach.careercoach.external.impl;

import com.coach.careercoach.dto.calcom.*;
import com.coach.careercoach.exception.CalComApiException;
import com.coach.careercoach.external.CalComClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * Cal.com API 真实实现
 * 使用Cal.com API v2
 */
@Component
@Primary
public class CalComClientImpl implements CalComClient {

    private static final Logger log = LoggerFactory.getLogger(CalComClientImpl.class);

    private final RestTemplate restTemplate;

    @Value("${cal.api-url}")
    private String apiUrl;

    @Value("${cal.api-key}")
    private String apiKey;

    @Value("${cal.base-url}")
    private String baseUrl;

    @Value("${cal.username}")
    private String username;

    @Value("${cal.event-type-slug}")
    private String eventTypeSlug;

    @Value("${cal.round-robin-event-type-id:}")
    private String roundRobinEventTypeId;

    public CalComClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getBookingUrl(Long userId) {
        try {
            // 如果配置了Round Robin Event Type ID，直接使用
            if (roundRobinEventTypeId != null && !roundRobinEventTypeId.isEmpty()) {
                return buildBookingUrl(roundRobinEventTypeId, userId);
            }

            // 否则查询Event Types，找到Round Robin类型
            EventTypeResponse response = getEventTypes();
            if (response != null && response.getData() != null) {
                // 查找Round Robin类型的Event Type
                for (EventTypeResponse.EventType eventType : response.getData()) {
                    if ("ROUND_ROBIN".equalsIgnoreCase(eventType.getSchedulingType())) {
                        log.info("Found Round Robin event type: {}", eventType.getId());
                        return buildBookingUrl(String.valueOf(eventType.getId()), userId);
                    }
                }
                
                // 如果没有Round Robin类型，使用第一个Event Type
                if (!response.getData().isEmpty()) {
                    EventTypeResponse.EventType firstEventType = response.getData().get(0);
                    log.warn("No Round Robin event type found, using first event type: {}", firstEventType.getId());
                    return buildBookingUrl(String.valueOf(firstEventType.getId()), userId);
                }
            }

            // 如果API调用失败，返回默认的预约链接
            log.warn("Failed to get event types, returning default booking URL");
            return String.format("%s/%s/%s?userId=%d", baseUrl, username, eventTypeSlug, userId);

        } catch (Exception e) {
            log.error("Error getting booking URL", e);
            // 返回默认的预约链接
            return String.format("%s/%s/%s?userId=%d", baseUrl, username, eventTypeSlug, userId);
        }
    }

    @Override
    public String getCancelUrl(String externalBookingId) {
        if (externalBookingId == null || externalBookingId.isEmpty()) {
            throw new IllegalArgumentException("External booking ID cannot be null or empty");
        }

        // Cal.com的取消链接格式
        return String.format("%s/booking/%s?cancel=true", baseUrl, externalBookingId);
    }

    /**
     * 获取所有Event Types
     */
    public EventTypeResponse getEventTypes() {
        try {
            String url = apiUrl + "/event-types";
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<EventTypeResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    EventTypeResponse.class
            );

            log.info("Got event types from Cal.com: {}", response.getBody());
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("Cal.com API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CalComApiException("Failed to get event types: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error calling Cal.com API", e);
            throw new CalComApiException("Failed to get event types: " + e.getMessage(), e);
        }
    }

    /**
     * 获取可用时间槽
     */
    public SlotResponse getAvailableSlots(Long eventTypeId, LocalDate startDate, LocalDate endDate) {
        try {
            String url = String.format("%s/slots/available?eventTypeId=%d&startTime=%s&endTime=%s",
                    apiUrl,
                    eventTypeId,
                    startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            );

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<SlotResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    SlotResponse.class
            );

            log.info("Got available slots from Cal.com");
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("Cal.com API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CalComApiException("Failed to get available slots: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error calling Cal.com API", e);
            throw new CalComApiException("Failed to get available slots: " + e.getMessage(), e);
        }
    }

    /**
     * 创建预约
     */
    public BookingApiResponse createBooking(Long eventTypeId, String startTime,
                                            String attendeeName, String attendeeEmail) {
        try {
            String url = apiUrl + "/bookings";

            BookingCreateRequest.Attendee attendee = BookingCreateRequest.Attendee.builder()
                    .name(attendeeName)
                    .email(attendeeEmail)
                    .timeZone("Asia/Shanghai")
                    .language("zh")
                    .build();

            BookingCreateRequest request = BookingCreateRequest.builder()
                    .eventTypeId(eventTypeId)
                    .start(startTime)
                    .attendee(Collections.singletonList(attendee))
                    .timeZone("Asia/Shanghai")
                    .language("zh")
                    .build();

            HttpHeaders headers = createHeaders();
            HttpEntity<BookingCreateRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<BookingApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    BookingApiResponse.class
            );

            log.info("Created booking on Cal.com: {}", response.getBody());
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("Cal.com API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CalComApiException("Failed to create booking: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error calling Cal.com API", e);
            throw new CalComApiException("Failed to create booking: " + e.getMessage(), e);
        }
    }

    /**
     * 取消预约
     */
    public BookingApiResponse cancelBooking(String bookingUid, String reason) {
        try {
            String url = apiUrl + "/bookings/" + bookingUid + "/cancel";

            CancelBookingRequest request = CancelBookingRequest.builder()
                    .cancellationReason(reason)
                    .build();

            HttpHeaders headers = createHeaders();
            HttpEntity<CancelBookingRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<BookingApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    BookingApiResponse.class
            );

            log.info("Cancelled booking on Cal.com: {}", bookingUid);
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("Cal.com API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CalComApiException("Failed to cancel booking: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error calling Cal.com API", e);
            throw new CalComApiException("Failed to cancel booking: " + e.getMessage(), e);
        }
    }

    /**
     * 查询预约详情
     */
    public BookingApiResponse getBooking(String bookingUid) {
        try {
            String url = apiUrl + "/bookings/" + bookingUid;
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<BookingApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    BookingApiResponse.class
            );

            log.info("Got booking from Cal.com: {}", bookingUid);
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("Cal.com API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CalComApiException("Failed to get booking: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error calling Cal.com API", e);
            throw new CalComApiException("Failed to get booking: " + e.getMessage(), e);
        }
    }

    /**
     * 构建预约链接
     */
    private String buildBookingUrl(String eventTypeId, Long userId) {
        return String.format("%s/%s/%s?userId=%d", baseUrl, username, eventTypeSlug, userId);
    }

    /**
     * 创建HTTP Headers
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        return headers;
    }
}

