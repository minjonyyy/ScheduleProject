package com.example.scheduleproject.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleRequestDto {

    private String title; // 제목
    private String task; // 할 일 내용
    private String username; // 작성자 이름
    private String password; // 비밀번호
    private LocalDateTime createdDate; //작성일
    private LocalDateTime modifiedDate; //수정일

    private String confirmPW;
}
