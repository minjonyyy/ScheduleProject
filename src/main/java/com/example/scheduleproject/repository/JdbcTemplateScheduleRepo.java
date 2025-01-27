package com.example.scheduleproject.repository;

import com.example.scheduleproject.dto.ScheduleResponseDto;
import com.example.scheduleproject.entity.Schedule;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class JdbcTemplateScheduleRepo implements ScheduleRepo{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateScheduleRepo(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ScheduleResponseDto saveSchedule(Schedule schedule) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("schedules").usingGeneratedKeyColumns("schedule_id");

        schedule.setCreatedDate(LocalDateTime.now());
        schedule.setModifiedDate(LocalDateTime.now());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", schedule.getTitle());
        parameters.put("task", schedule.getTask());
        parameters.put("username", schedule.getUsername());
        parameters.put("password", schedule.getPassword());
        parameters.put("created_at", schedule.getCreatedDate());
        parameters.put("modified_at", schedule.getModifiedDate());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));

        return new ScheduleResponseDto(
                key.longValue(),
                schedule.getTitle(),
                schedule.getTask(),
                schedule.getUsername(),
                schedule.getCreatedDate(),
                schedule.getModifiedDate()
        );
    }

    @Override
    public List<ScheduleResponseDto> findAllSchedules() {
        return jdbcTemplate.query("select * from schedules", scheduleRowMapper());
    }

    @Override
    public List<ScheduleResponseDto> findSchedulesWithFilters(LocalDate filterDate, String keyword) {
        String sql = "select * from schedules where TRUE";
        List<Object> params = new ArrayList<>();

        if(filterDate != null) {
            sql += " AND DATE(modified_at) <= ?";
            params.add(java.sql.Date.valueOf(filterDate));
        }

        if(keyword != null) {
            sql += " AND username = ?";
            params.add(keyword);
        }
        sql += "order by modified_at desc";

        return jdbcTemplate.query(sql, scheduleRowMapper(), params.toArray());
    }

    @Override
    public Optional<Schedule> findScheduleById(Long id) {
        List<Schedule> result = jdbcTemplate.query("select * from schedules where schedule_id=?", scheduleRowMapperV2(), id);
        return result.stream().findAny();
    }

    @Override
    public Schedule findScheduleByIdOrElseThrow(Long id) {
        List<Schedule> result = jdbcTemplate.query("select * from schedules where schedule_id=?", scheduleRowMapperV2(), id);
        return result.stream().findAny().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exists id = " + id));
    }

    @Override
    public int updateSchedule(Long id, String title, String task, String username) {
        return jdbcTemplate.update("update schedules set title = ?, task = ?, username = ? where schedule_id=?", title, task, username, id);
    }

    @Override
    public int deleteScheduleById(Long id) {
        return jdbcTemplate.update("delete from schedules where schedule_id=?", id);
    }



    private RowMapper<ScheduleResponseDto> scheduleRowMapper() {
        return new RowMapper<ScheduleResponseDto>() {
            @Override
            public ScheduleResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new ScheduleResponseDto(
                        rs.getLong("schedule_id"),
                        rs.getString("title"),
                        rs.getString("task"),
                        rs.getString("username"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("modified_at").toLocalDateTime()
                );
            }
        };
    }
    private RowMapper<Schedule> scheduleRowMapperV2() {
        return new RowMapper<Schedule>() {
            @Override
            public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Schedule(
                        rs.getLong("schedule_id"),
                        rs.getString("title"),
                        rs.getString("task"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("modified_at").toLocalDateTime()
                );
            }
        };
    }
}
