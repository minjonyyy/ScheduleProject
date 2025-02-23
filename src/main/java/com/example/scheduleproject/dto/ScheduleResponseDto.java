package com.example.scheduleproject.dto;

import com.example.scheduleproject.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleResponseDto {

    private Long scheduleId; // 일정의 고유 id
    private String title; // 제목
    private String task; // 할 일 내용
    private String username; // 작성자 이름
    private LocalDateTime createdDate; //작성일
    private LocalDateTime modifiedDate; //수정일

    public ScheduleResponseDto(Schedule schedule) {
        this.scheduleId = schedule.getScheduleId();
        this.title = schedule.getTitle();
        this.task = schedule.getTask();
        this.username = schedule.getUsername();
        this.createdDate = schedule.getCreatedDate();
        this.modifiedDate = schedule.getModifiedDate();
    }
}
