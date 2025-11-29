package com.coach.careercoach.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coach.careercoach.model.entity.CoachProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 导师信息表 Mapper 接口
 */
@Mapper
public interface CoachProfileMapper extends BaseMapper<CoachProfile> {
}

