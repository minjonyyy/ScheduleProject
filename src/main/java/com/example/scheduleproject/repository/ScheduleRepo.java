package com.example.scheduleproject.repository;

import com.example.scheduleproject.dto.ScheduleListResponseDto;
import com.example.scheduleproject.dto.ScheduleResponseDto;
import com.example.scheduleproject.entity.Schedule;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepo {
    ScheduleResponseDto saveSchedule(Schedule schedule);

    List<ScheduleResponseDto> findAllSchedules();

    List<ScheduleResponseDto> findSchedulesWithFilters(LocalDate filterDate, BigInteger userId);

    Optional<Schedule> findScheduleById(Long id);

    Schedule findScheduleByIdOrElseThrow(Long id);

    int updateSchedule(Long id, String title, String task, String username);

    int deleteScheduleById(Long id);


    List<ScheduleListResponseDto> findScheduleWithPage(int pageNum, int pageSize);
}
