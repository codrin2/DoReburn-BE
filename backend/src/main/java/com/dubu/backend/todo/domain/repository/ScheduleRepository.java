package com.dubu.backend.todo.domain.repository;

import com.dubu.backend.todo.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleQueryRepository{
    Optional<Schedule> findTopByMemberIdAndDateLessThanEqualOrderByDateDesc(Long memberId, LocalDate date);

    Long memberId(Long memberId);
}
