package com.dubu.backend.todo.domain.service;

import com.dubu.backend.todo.domain.Schedule;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ScheduleDateService {

    public boolean hasBeenChanged(Schedule schedule) {
        return schedule.matchesDate(LocalDate.now().plusDays(1));
    }
}
