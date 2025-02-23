package com.dubu.backend.todo.application;

import com.dubu.backend.todo.dto.response.DayStatisticInfo;
import com.dubu.backend.todo.dto.response.WeekStatisticInfo;

import java.time.LocalDate;

public interface StatisticService {
    DayStatisticInfo collectDayStatistic(Long memberId, LocalDate date);
    WeekStatisticInfo collectWeekStatistic(Long memberId, LocalDate date);
}