package com.example.scheduleproject.service;

import com.example.scheduleproject.dto.ScheduleRequestDto;
import com.example.scheduleproject.dto.ScheduleResponseDto;
import com.example.scheduleproject.entity.Schedule;
import com.example.scheduleproject.repository.ScheduleRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepo scheduleRepo;

    public ScheduleServiceImpl(ScheduleRepo scheduleRepo) {
        this.scheduleRepo = scheduleRepo;
    }


    @Override
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto dto) {

        Schedule schedule = new Schedule(dto.getTitle(), dto.getTask(), dto.getUsername(), dto.getPassword(), LocalDateTime.now(), LocalDateTime.now());

        return new ScheduleResponseDto(scheduleRepo.saveSchedule(schedule));


    }

    @Override
    public List<ScheduleResponseDto> findAllSchedules() {
        List<ScheduleResponseDto> allSchedules = scheduleRepo.findAllSchedules();
        return allSchedules;
    }

    @Override
    public ScheduleResponseDto findScheduleById(Long id) {
        Schedule schedule = scheduleRepo.findScheduleById(id);

        if (schedule == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
        }

        return new ScheduleResponseDto(schedule);
    }


    @Override
    public ScheduleResponseDto updateSchedule(Long id, String confirmPW, String title, String task, String username) {

        Schedule schedule = scheduleRepo.findScheduleById(id);

        if (schedule == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
        }

        if (task == null || username == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The title and content are required values.");
        }

        if(confirmPW.equals(schedule.getPassword())) {
            schedule.update(title, task, username);
        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return null;
    }

    @Override
    public void deleteScheduleById(Long id, String confirmPW) {
        Schedule schedule = scheduleRepo.findScheduleById(id);
        if (schedule == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
        }

        if(confirmPW.equals(schedule.getPassword())) {
            scheduleRepo.deleteSchduleById(id);
        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);

    }


}
