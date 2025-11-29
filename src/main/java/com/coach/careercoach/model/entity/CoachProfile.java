package com.coach.careercoach.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 教练信息实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("cc_coach_profile")
public class CoachProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String calCoachId;

    private String name;

    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
