package com.example.scheduleproject.service;

import com.example.scheduleproject.dto.ScheduleRequestDto;
import com.example.scheduleproject.dto.ScheduleResponseDto;
import com.example.scheduleproject.entity.Schedule;
import com.example.scheduleproject.repository.ScheduleRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepo scheduleRepo;

    public ScheduleServiceImpl(ScheduleRepo scheduleRepo) {
        this.scheduleRepo = scheduleRepo;
    }


    @Override
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto dto) {

        Schedule schedule = new Schedule(dto.getTitle(), dto.getTask(), dto.getUsername(), dto.getUserEmail() ,dto.getPassword(), LocalDateTime.now(), LocalDateTime.now());

        return scheduleRepo.saveSchedule(schedule);
    }

    @Override
    public List<ScheduleResponseDto> findAllSchedules() {
        List<ScheduleResponseDto> allSchedules = scheduleRepo.findAllSchedules();
        return allSchedules;
    }

    @Override
    public List<ScheduleResponseDto> findSchedulesWithFilters(LocalDate filterDate, BigInteger userId) {
        return scheduleRepo.findSchedulesWithFilters(filterDate, userId);
    }

    @Override
    public ScheduleResponseDto findScheduleById(Long id) {
        Schedule schedule = scheduleRepo.findScheduleByIdOrElseThrow(id);

        return new ScheduleResponseDto(schedule);
    }

    @Transactional
    @Override
    public ScheduleResponseDto updateSchedule(Long id, String confirmPW, String title, String task, String username) {
        if (task == null || username == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The title and content are required values.");
        }

        Schedule existingSchedule = scheduleRepo.findScheduleByIdOrElseThrow(id);

        if(confirmPW.equals(existingSchedule.getPassword())) {
            int updatedRow = scheduleRepo.updateSchedule(id, title, task, username);
            if (updatedRow == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
            }
        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다.");

        Schedule schedule = scheduleRepo.findScheduleByIdOrElseThrow(id);

        return new ScheduleResponseDto(schedule);
    }

    @Override
    public void deleteScheduleById(Long id, String confirmPW) {

        Optional<Schedule> existingSchedule = scheduleRepo.findScheduleById(id);

        if(confirmPW.equals(existingSchedule.get().getPassword())) {
            int deletedRow = scheduleRepo.deleteScheduleById(id);
            if (deletedRow == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
            }
        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다.");

    }




}
