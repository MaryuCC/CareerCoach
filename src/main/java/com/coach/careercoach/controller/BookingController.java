package com.coach.careercoach.controller;

import com.coach.careercoach.api.ApiResponse;
import com.coach.careercoach.dto.booking.*;
import com.coach.careercoach.dto.webhook.CalWebhookPayload;
import com.coach.careercoach.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约管理控制器
 */
@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * 获取可用时间槽
     * GET /api/available-slots?startDate=2024-12-01&endDate=2024-12-31
     */
    @GetMapping("/available-slots")
    public ApiResponse<AvailableSlotsResponse> getAvailableSlots(
            @RequestParam Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        // 默认查询未来30天
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now();
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now().plusDays(30);
        
        AvailableSlotsResponse response = bookingService.getAvailableSlots(userId, start, end);
        return ApiResponse.ok(response);
    }

    /**
     * 获取预约链接（用户选择时间槽后）
     * POST /api/booking-url?userId={userId}
     */
    @PostMapping("/booking-url")
    public ApiResponse<BookingUrlResponse> getBookingUrl(@RequestParam Long userId) {
        String url = bookingService.getBookingUrl(userId);
        return ApiResponse.ok(new BookingUrlResponse(url));
    }

    /**
     * 查询我的预约列表
     * GET /api/bookings?userId={userId}
     */
    @GetMapping("/bookings")
    public ApiResponse<List<BookingDetailVO>> listMyBookings(@RequestParam Long userId) {
        List<BookingDetailVO> bookings = bookingService.listUserBookings(userId);
        return ApiResponse.ok(bookings);
    }

    /**
     * 获取取消预约链接
     * POST /api/bookings/cancel
     */
    @PostMapping("/bookings/cancel")
    public ApiResponse<CancelUrlResponse> getCancelUrl(@RequestBody CancelBookingRequestDTO request) {
        String url = bookingService.getCancelUrl(request.getBookingId());
        return ApiResponse.ok(new CancelUrlResponse(url, "请访问该链接取消预约"));
    }

    /**
     * 接收Cal.com Webhook
     * POST /api/webhook/cal
     */
    @PostMapping("/webhook/cal")
    public ApiResponse<String> handleCalWebhook(@RequestBody CalWebhookPayload payload) {
        try {
            bookingService.handleWebhook(payload);
            return ApiResponse.ok("Webhook处理成功");
        } catch (Exception e) {
            return ApiResponse.fail("Webhook处理失败: " + e.getMessage());
        }
    }
}
