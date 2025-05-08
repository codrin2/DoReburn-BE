package com.dubu.backend.plan.api;

import com.dubu.backend.core.domain.SuccessResponse;
import com.dubu.backend.plan.api.response.DailyStatisticsResponse;
import com.dubu.backend.plan.api.response.WeeklyStatisticsResponse;
import com.dubu.backend.plan.application.StatisticsFacade;
import com.dubu.backend.plan.application.dto.DailyStatisticsResult;
import com.dubu.backend.plan.application.dto.WeeklyStatisticsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController implements StatisticsApi{
    private final StatisticsFacade statisticsFacade;

    @GetMapping("/day")
    public SuccessResponse<?> getDayStatistics(
            @RequestAttribute Long memberId,
            @RequestParam LocalDate date
            ){
        DailyStatisticsResult result = statisticsFacade.calculateDayStatistics(memberId, date);

        return SuccessResponse.of(DailyStatisticsResponse.from(result));
    }

    @GetMapping("/week")
    public SuccessResponse<?> getWeekStatistics(
            @RequestAttribute Long memberId,
            @RequestParam LocalDate date
        ){
        WeeklyStatisticsResult result = statisticsFacade.calculateWeeklyStatistics(memberId, date);

        return SuccessResponse.of(WeeklyStatisticsResponse.from(result));
    }

}

