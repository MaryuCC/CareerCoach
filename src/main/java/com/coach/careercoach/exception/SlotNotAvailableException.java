package com.coach.careercoach.exception;

/**
 * 时间槽不可用异常
 */
public class SlotNotAvailableException extends BusinessException {

    public SlotNotAvailableException(String slotTime) {
        super("SLOT_NOT_AVAILABLE", "时间槽不可用: " + slotTime);
    }
}

