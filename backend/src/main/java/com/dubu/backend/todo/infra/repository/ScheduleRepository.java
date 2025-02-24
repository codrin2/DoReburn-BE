package com.dubu.backend.todo.infra.repository;

import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.infra.repository.querydsl.CustomScheduleRepository;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleRepository extends JpaRepository<Schedule, Long>, CustomScheduleRepository {
}