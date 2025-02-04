package com.example.scheduleproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleRequestDto {

    private String title; // 제목

    @NotBlank(message = "할 일 내용은 필수 입력 항목입니다.")
    @Size(max = 200, message = "할 일 내용은 최대 200자까지 입력 가능합니다.")
    private String task; // 할 일 내용

    private String username; // 작성자 이름

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String userEmail;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password; // 비밀번호

    private LocalDateTime createdDate; //작성일
    private LocalDateTime modifiedDate; //수정일

    private String confirmPW;
}
