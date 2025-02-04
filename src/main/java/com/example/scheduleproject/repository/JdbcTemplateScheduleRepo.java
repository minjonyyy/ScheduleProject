package com.example.scheduleproject.repository;

import com.example.scheduleproject.dto.ScheduleListResponseDto;
import com.example.scheduleproject.dto.ScheduleResponseDto;
import com.example.scheduleproject.entity.Schedule;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.*;
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
        try {
            // 사용자가 이미 존재하는지 확인
            String checkUserSql = "SELECT user_id FROM users WHERE user_email = ?";

            Long existingUserId = null;
            try {
                existingUserId = jdbcTemplate.queryForObject(checkUserSql, Long.class, schedule.getUserEmail());
            } catch (EmptyResultDataAccessException e) {
                // 사용자 존재하지 않으면 새로운 사용자 생성 로직으로 넘어감
                existingUserId = null;
            }

            // 사용자가 존재하지 않으면 users 테이블에 사용자 정보 삽입
            if (existingUserId == null) {
                String insertUserSql = "INSERT INTO users (user_name, user_email) VALUES (?, ?)";
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, schedule.getUsername());
                    ps.setString(2, schedule.getUserEmail());
                    return ps;
                }, keyHolder);

                // 사용자 ID 가져오기
                existingUserId = keyHolder.getKey().longValue();
            }

            // schedules 테이블에 일정 정보 저장
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName("schedules").usingGeneratedKeyColumns("schedule_id");

            LocalDateTime now = LocalDateTime.now();
            schedule.setCreatedDate(now);
            schedule.setModifiedDate(now);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("title", schedule.getTitle());
            parameters.put("task", schedule.getTask());
            parameters.put("username", schedule.getUsername());
            parameters.put("useremail", schedule.getUserEmail());
            parameters.put("created_at", schedule.getCreatedDate());
            parameters.put("modified_at", schedule.getModifiedDate());
            parameters.put("user_id", existingUserId);  // 외래키로 사용자 ID 추가
            parameters.put("password", schedule.getPassword());

            Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));

            return new ScheduleResponseDto(
                    key.longValue(),
                    schedule.getTitle(),
                    schedule.getTask(),
                    schedule.getUsername(),
                    schedule.getCreatedDate(),
                    schedule.getModifiedDate()
            );
        } catch (DataAccessException e) {
            throw new RuntimeException("DB 작업 중 오류 발생", e);
        }
    }


    @Override
    public List<ScheduleResponseDto> findAllSchedules() {
        return jdbcTemplate.query("select * from schedules", scheduleRowMapper());
    }

    @Override
    public List<ScheduleResponseDto> findSchedulesWithFilters(LocalDate filterDate, BigInteger userId) {
        String sql = "select * from schedules where TRUE";
        List<Object> params = new ArrayList<>();

        if(filterDate != null) {
            sql += " AND DATE(modified_at) <= ?";
            params.add(java.sql.Date.valueOf(filterDate));
        }

        if(userId != null) {
            sql += " AND user_id = ?";
            params.add(userId);
        }
        sql += " order by modified_at desc"; //수정일 기준 내림차순

        return jdbcTemplate.query(sql, scheduleRowMapper(), params.toArray());
    }

    @Override
    public List<ScheduleListResponseDto> findScheduleWithPage(int pageNum, int pageSize) {
        String countSql = "SELECT COUNT(*) FROM schedules"; // 일정 갯수 세기
        int totalCount = jdbcTemplate.queryForObject(countSql, Integer.class);
        if (totalCount == 0 || pageNum * pageSize >= totalCount){ // 만약 일정이 아예 없거나, 범위를 넘어선 페이지를 요청하는 경우
            return Collections.emptyList(); //빈 배열 반환
        }

        String sql = "SELECT * FROM schedules ORDER BY modified_at DESC LIMIT ? OFFSET ?";
        List<Object> params = new ArrayList<>();
        params.add(pageSize);
        params.add(pageNum*pageSize);

        return jdbcTemplate.query(sql, scheduleRowMapperV3(), params.toArray());

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
                        rs.getString("useremail"),
                        rs.getString("password"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("modified_at").toLocalDateTime()
                );
            }
        };
    }

    private RowMapper<ScheduleListResponseDto> scheduleRowMapperV3(){
        return new RowMapper<ScheduleListResponseDto>() {
            @Override
            public ScheduleListResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new ScheduleListResponseDto(
                        rs.getLong("schedule_id"),
                        rs.getString("title"),
                        rs.getString("username"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("modified_at").toLocalDateTime()
                );
            }
        };
    }
}
