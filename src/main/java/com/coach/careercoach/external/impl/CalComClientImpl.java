package com.coach.careercoach.external.impl;

import com.coach.careercoach.dto.calcom.*;
import com.coach.careercoach.exception.CalComApiException;
import com.coach.careercoach.external.CalComClient;
import com.coach.careercoach.model.entity.User;
import com.coach.careercoach.service.UserService;
import com.coach.careercoach.util.EncryptionUtil;
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

/**
 * Cal.com 功能实现
 */
@Component
@Primary
public class CalComClientImpl implements CalComClient {

    private static final Logger log = LoggerFactory.getLogger(CalComClientImpl.class);

    private final RestTemplate restTemplate;
    private final UserService userService;
    private final EncryptionUtil encryptionUtil;

    @Value("${cal.api-url}")
    private String apiUrl;

    @Value("${cal.base-url}")
    private String baseUrl;

    @Value("${cal.username}")
    private String username;

    @Value("${cal.event-type-slug}")
    private String eventTypeSlug;
    
    @Value("${cal.event-type-id}")
    private String eventTypeId;

    @Value("${cal.time-zone:Asia/Shanghai}")
    private String timeZone;

    public CalComClientImpl(RestTemplate restTemplate, UserService userService, EncryptionUtil encryptionUtil) {
        this.restTemplate = restTemplate;
        this.userService = userService;
        this.encryptionUtil = encryptionUtil;
    }



    @Override
    public String getBookingUrl(Long userId) {
        // 构建 Cal.com 预约页面链接
        // 格式: https://cal.com/{username}/{event-type-slug}
        String bookingUrl = String.format("%s/%s/%s", baseUrl, username, eventTypeSlug);
        log.info("Generated booking URL for userId {}: {}", userId, bookingUrl);
        return bookingUrl;
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
     * 获取可用时间槽
     */
    @Override
    public SlotResponse getAvailableSlots(Long userId, Long eventTypeId, LocalDate startDate, LocalDate endDate) {
        try {
            // 0. 从数据库获取API Key
            String apiKey = getApiKey(userId);
            
            // 1. 转换为ISO 8601格式（带时区）
            String start = startDate.atStartOfDay()
                    .atZone(java.time.ZoneId.of("UTC"))
                    .format(DateTimeFormatter.ISO_INSTANT);  // 2025-12-01T00:00:00Z
            
            String end = endDate.atTime(23, 59, 59)
                    .atZone(java.time.ZoneId.of("UTC"))
                    .format(DateTimeFormatter.ISO_INSTANT);  // 2025-12-08T23:59:59Z
            
            // 2. 构造URL - 正确的端点和参数
            String url = String.format("%s/slots?eventTypeId=%d&start=%s&end=%s&timeZone=%s",
                    apiUrl,           // https://api.cal.com/v2
                    eventTypeId,
                    start,            // 2025-12-01T00:00:00Z
                    end,              // 2025-12-08T23:59:59Z
                    timeZone          // Asia/Shanghai or Australia/Sydney
            );

            // 3. 设置Headers - 添加API version
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("cal-api-version", "2024-09-04");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<SlotResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    SlotResponse.class
            );

            log.info("Got available slots from Cal.com for userId: {}, eventTypeId: {}", userId, eventTypeId);
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
     * 获取用户的预约列表
     */
    @Override
    public CalBookingResponse getUserBookings(Long userId, String attendeeEmail) {
        try {
            // 0. 从数据库获取API Key
            String apiKey = getApiKey(userId);
            
            // 1. 构造URL - 使用attendeeEmail参数过滤
            String url = String.format("%s/bookings?attendeeEmail=%s&sortCreated=desc",
                    apiUrl,           // https://api.cal.com/v2
                    attendeeEmail
            );

            // 2. 设置Headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("cal-api-version", "2024-08-13");  // 使用文档中指定的版本
            
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 3. 调用API
            ResponseEntity<CalBookingResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    CalBookingResponse.class
            );

            log.info("Got bookings from Cal.com for userId: {}, email: {}", userId, attendeeEmail);
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("Cal.com API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CalComApiException("Failed to get user bookings: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error calling Cal.com API", e);
            throw new CalComApiException("Failed to get user bookings: " + e.getMessage(), e);
        }
    }

    /**
     * 从数据库获取用户的API Key并解密
     */
    private String getApiKey(Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new CalComApiException("用户不存在: " + userId);
        }
        if (user.getApikeyHash() == null || user.getApikeyHash().isEmpty()) {
            throw new CalComApiException("用户未配置API Key: " + userId);
        }

        // 解密 API Key
        try {
            return encryptionUtil.decrypt(user.getApikeyHash());
        } catch (Exception e) {
            throw new CalComApiException("API Key 解密失败: " + e.getMessage(), e);
        }
    }

}

