package com.example.scheduleproject.controller;

import com.example.scheduleproject.dto.ScheduleRequestDto;
import com.example.scheduleproject.dto.ScheduleResponseDto;
import com.example.scheduleproject.entity.Schedule;
import com.example.scheduleproject.service.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedules")
public class scheduleController {

    private final ScheduleService scheduleService;

    public scheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(@RequestBody ScheduleRequestDto dto) {

        return new ResponseEntity<>(scheduleService.saveSchedule(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<ScheduleResponseDto> findAllSchedules() {

        return scheduleService.findAllSchedules();
    }

    @GetMapping("/filter")
    public List<ScheduleResponseDto> findSchedulesWithFilters(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate filterDate,
            @RequestParam(required = false) BigInteger userId
    ){
        return scheduleService.findSchedulesWithFilters(filterDate, userId);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> findScheduleById(@PathVariable Long id) {

        return new ResponseEntity<>(scheduleService.findScheduleById(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto dto){

        return new ResponseEntity<>(scheduleService.updateSchedule(id, dto.getConfirmPW(), dto.getTitle(), dto.getTask(), dto.getUsername()), HttpStatus.OK);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleById(@PathVariable Long id, @RequestBody ScheduleRequestDto dto) {

        scheduleService.deleteScheduleById(id, dto.getConfirmPW());

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
