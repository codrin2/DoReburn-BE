package com.dubu.backend.statistic.service.collection;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PathIdDateCollection {
    private final Map<Long, LocalDate> pathIdDate = new HashMap<>();

    public void putLocalDate(Long pathId, LocalDate date){
        pathIdDate.put(pathId, date);
    }

    public LocalDate getLocalDate(Long pathId){
        return pathIdDate.get(pathId);
    }
}


