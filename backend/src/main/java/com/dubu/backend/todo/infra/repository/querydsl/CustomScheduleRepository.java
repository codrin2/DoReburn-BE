package com.dubu.backend.todo.infra.repository.querydsl;

import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.todo.domain.Schedule;

import java.time.LocalDate;
import java.util.Optional;

public interface CustomScheduleRepository {
    Optional<Schedule> findLatestSchedule(Member member, LocalDate date);
}