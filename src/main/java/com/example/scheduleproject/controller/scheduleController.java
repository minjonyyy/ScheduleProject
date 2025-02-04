package com.example.scheduleproject.controller;

import com.example.scheduleproject.dto.ScheduleListResponseDto;
import com.example.scheduleproject.dto.ScheduleRequestDto;
import com.example.scheduleproject.dto.ScheduleResponseDto;
import com.example.scheduleproject.entity.Schedule;
import com.example.scheduleproject.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.time.LocalDate;

import java.util.*;


@RestController
@RequestMapping("/schedules")
public class scheduleController {

    private final ScheduleService scheduleService;

    public scheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 할 일 저장하는 함수
    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(@Valid @RequestBody ScheduleRequestDto dto) {

        return new ResponseEntity<>(scheduleService.saveSchedule(dto), HttpStatus.CREATED);
    }

    // 모든 스케쥴 조회 함수
    @GetMapping
    public List<ScheduleResponseDto> findAllSchedules() {

        return scheduleService.findAllSchedules();
    }

    // 수정일과 userId를 조건으로 조회하는 함수
    @GetMapping("/filter")
    public List<ScheduleResponseDto> findSchedulesWithFilters(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate filterDate,
            @RequestParam(required = false) BigInteger userId
    ){
        return scheduleService.findSchedulesWithFilters(filterDate, userId);
    }

    // 페이지네이션
    // localhost:8080/schedules/page?pageNum=0&pageSize=10 으로 요청
    // 요청사항 없으면 기본 pageNum=0, pageSize=5로 설정함
    @GetMapping("/page")
    public List<ScheduleListResponseDto> findSchedulesWithPage(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ){
        return scheduleService.findScheduleWithPage(pageNum, pageSize);
    }

    // schedule id로 단건 조회 함수
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> findScheduleById(@PathVariable Long id) {

        return new ResponseEntity<>(scheduleService.findScheduleById(id), HttpStatus.OK);
    }

    // schedule id로 수정 함수
    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto dto){

        return new ResponseEntity<>(scheduleService.updateSchedule(id, dto.getConfirmPW(), dto.getTitle(), dto.getTask(), dto.getUsername()), HttpStatus.OK);

    }

    // schedule id로 삭제 함수
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleById(@PathVariable Long id, @RequestBody ScheduleRequestDto dto) {

        scheduleService.deleteScheduleById(id, dto.getConfirmPW());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 예외 처리 함수
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException e) {
        if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getReason());
        }
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getReason());
        }
        return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
    }


}
