package com.coach.careercoach.dto.coach;

import lombok.Data;

/**
 * 教练查询请求
 */
@Data
public class CoachQueryRequest {

    private String city;
    private String tag;
    private String status;
    private String keyword;
}
