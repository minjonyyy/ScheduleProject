package com.example.scheduleproject.entity;

import com.example.scheduleproject.dto.ScheduleRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Schedule {
    //할일, 작성자명, 비밀번호, 작성/수정일
    @Setter
    private Long scheduleId; // 일정의 고유 id
    private String title; // 제목
    private String task; // 할 일 내용
    private String username; // 작성자 이름
    private String password; // 비밀번호
    private LocalDateTime createdDate; //작성일
    private LocalDateTime modifiedDate; //수정일

    public Schedule (String title, String task, String username, String password, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.title = title;
        this.task = task;
        this.username = username;
        this.password = password;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }



    public void updateSchedule(ScheduleRequestDto dto) {
        this.title = dto.getTitle();
        this.task = dto.getTask();
        this.username = dto.getUsername();
        this.modifiedDate = LocalDateTime.now();
    }

    public void update(String title, String task, String username) {
        this.title = title;
        this.task = task;
        this.username = username;
        this.modifiedDate = LocalDateTime.now();
    }
}
