package com.coach.careercoach.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coach.careercoach.model.entity.Booking;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约表 Mapper 接口
 */
@Mapper
public interface BookingMapper extends BaseMapper<Booking> {
}

