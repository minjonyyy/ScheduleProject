package com.example.scheduleproject.controller;

import com.example.scheduleproject.dto.ScheduleRequestDto;
import com.example.scheduleproject.dto.ScheduleResponseDto;
import com.example.scheduleproject.entity.Schedule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedules")
public class scheduleController {

    private final Map<Long, Schedule> scheduleList = new HashMap<>();

    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(@RequestBody ScheduleRequestDto dto) {
        Long scheduleId = scheduleList.isEmpty() ? 1 : Collections.max(scheduleList.keySet()) + 1;

        Schedule schedule = new Schedule(scheduleId, dto.getTitle(), dto.getTask(), dto.getUsername(), dto.getPassword(), LocalDateTime.now(), LocalDateTime.now());

        scheduleList.put(scheduleId, schedule);

        return new ResponseEntity<>(new ScheduleResponseDto(schedule), HttpStatus.CREATED);
    }

    @GetMapping
    public List<ScheduleResponseDto> findAllSchedules() {

        return scheduleList.values().stream().map(ScheduleResponseDto::new).collect(Collectors.toList());

    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> findScheduleById(@PathVariable Long id) {

        Schedule schedule = scheduleList.get(id);

        return new ResponseEntity<>(new ScheduleResponseDto(schedule), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto dto){
        Schedule schedule = scheduleList.get(id);

        if(schedule == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (scheduleList.containsKey(id)) {
            if(dto.getConfirmPW().equals(schedule.getPassword())) {
                schedule.updateSchedule(dto);
            } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleById(@PathVariable Long id, @RequestBody ScheduleRequestDto dto) {
        Schedule schedule = scheduleList.get(id);

        if (scheduleList.containsKey(id)) {

            if(dto.getConfirmPW().equals(schedule.getPassword())) {
                scheduleList.remove(id);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
