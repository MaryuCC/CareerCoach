package com.coach.careercoach.exception;

/**
 * Event Type未找到异常
 */
public class EventTypeNotFoundException extends BusinessException {

    public EventTypeNotFoundException(String eventTypeId) {
        super("EVENT_TYPE_NOT_FOUND", "Event Type不存在: " + eventTypeId);
    }
}

