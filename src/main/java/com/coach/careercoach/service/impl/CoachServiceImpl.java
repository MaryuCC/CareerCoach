package com.coach.careercoach.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coach.careercoach.dto.coach.CoachQueryRequest;
import com.coach.careercoach.mapper.CoachProfileMapper;
import com.coach.careercoach.model.entity.CoachProfile;
import com.coach.careercoach.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoachServiceImpl implements CoachService {

    @Autowired
    private CoachProfileMapper coachProfileMapper;

    @Override
    public List<CoachProfile> list(CoachQueryRequest query, long page, long size) {
        LambdaQueryWrapper<CoachProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CoachProfile::getCreatedAt);

        Page<CoachProfile> pageObj = new Page<>(page, size);
        Page<CoachProfile> result = coachProfileMapper.selectPage(pageObj, wrapper);
        
        return result.getRecords();
    }

    @Override
    public CoachProfile findByCalCoachId(String calCoachId) {
        LambdaQueryWrapper<CoachProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoachProfile::getCalCoachId, calCoachId);
        return coachProfileMapper.selectOne(wrapper);
    }
}
