package com.example.scheduleproject.repository;

import com.example.scheduleproject.dto.ScheduleResponseDto;
import com.example.scheduleproject.entity.Schedule;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ScheduleRepoImpl implements ScheduleRepo {

    private final Map<Long, Schedule> scheduleList = new HashMap<>();

    @Override
    public Schedule saveSchedule(Schedule schedule) {
        Long scheduleId = scheduleList.isEmpty() ? 1 : Collections.max(scheduleList.keySet()) + 1;
        schedule.setScheduleId(scheduleId);

        scheduleList.put(scheduleId, schedule);
        return schedule;

    }

    @Override
    public List<ScheduleResponseDto> findAllSchedules() {
        List<ScheduleResponseDto> allSchedules = new ArrayList<>();

        for (Schedule schedule : scheduleList.values()) {
            ScheduleResponseDto dto = new ScheduleResponseDto(schedule);
            allSchedules.add(dto);
        }
        return allSchedules;

    }

    @Override
    public Schedule findScheduleById(Long id) {
        return scheduleList.get(id);
    }

    @Override
    public void deleteSchduleById(Long id) {
        scheduleList.remove(id);
    }
}
