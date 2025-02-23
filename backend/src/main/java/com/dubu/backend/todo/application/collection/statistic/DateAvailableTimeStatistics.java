package com.dubu.backend.todo.application.collection.statistic;

import lombok.Getter;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class DateAvailableTimeStatistics {
    private final Map<LocalDate, Integer> dateUsageTime;

    public DateAvailableTimeStatistics(LocalDate startDate) {
        dateUsageTime = IntStream.range(0, 7)
                .boxed()
                .collect(Collectors.toMap(
                        i -> startDate.plusDays(i),
                        i -> 0,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public void addUsageTimeAtDate(LocalDate date, int time){
        dateUsageTime.merge(date, time, Integer::sum);
    }
}