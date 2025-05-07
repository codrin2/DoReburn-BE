package com.dubu.backend.todo.domain.repository;

import com.dubu.backend.todo.domain.Schedule;

import java.time.LocalDate;
import java.util.Optional;

public interface ScheduleQueryRepository {
    Optional<Schedule> findLatestSchedule(Long memberId, LocalDate date);
}
