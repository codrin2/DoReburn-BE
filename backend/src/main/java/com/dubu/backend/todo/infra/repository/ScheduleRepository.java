package com.dubu.backend.todo.infra.repository;

import com.dubu.backend.todo.domain.Member;
import com.dubu.backend.todo.domain.past.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Member member(com.dubu.backend.member.domain.model.Member member);
}