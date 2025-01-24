package com.example.scheduleproject.repository;

import com.example.scheduleproject.entity.Schedule;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ScheduleRepoImpl implements ScheduleRepo {

    private final Map<Long, Schedule> scheduleList = new HashMap<>();
}
