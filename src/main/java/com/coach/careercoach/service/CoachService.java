package com.coach.careercoach.service;

import com.coach.careercoach.dto.coach.CoachQueryRequest;
import com.coach.careercoach.model.entity.CoachProfile;

import java.util.List;

public interface CoachService {

    List<CoachProfile> list(CoachQueryRequest query, long page, long size);

    CoachProfile findByCalCoachId(String calCoachId);
}
