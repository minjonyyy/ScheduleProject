package com.example.scheduleproject.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Schedule {
    //할일, 작성자명, 비밀번호, 작성/수정일
    private Long scheduleId; // 일정의 고유 id
    private String title; // 제목
    private String task; // 할 일 내용
    private String username; // 작성자 이름
    private String password; // 비밀번호
    private LocalDateTime createdDate; //작성일
    private LocalDateTime modifiedDate; //수정일


}
