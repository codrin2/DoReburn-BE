package com.dubu.backend.plan.application;

import com.dubu.backend.plan.application.api.MemberApi;
import com.dubu.backend.plan.application.dto.DailyStatisticsResult;
import com.dubu.backend.plan.application.dto.WeeklyStatisticsResult;
import com.dubu.backend.plan.domain.Feedback;
import com.dubu.backend.plan.domain.Member;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.service.StatisticsService;
import com.dubu.backend.plan.domain.vo.DailyStats;
import com.dubu.backend.plan.domain.vo.WeeklyFeedbackStats;
import com.dubu.backend.plan.domain.vo.WeeklyTodoStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsFacade {
    private final PlanFacade planFacade;

    private final StatisticsService statisticsService;

    private final MemberApi memberApi;

    public DailyStatisticsResult calculateDayStatistics(Long memberId, LocalDate date) {
        Member member = memberApi.getMember(memberId);

        List<Plan> plans = planFacade.findPlans(member, date.atStartOfDay(), date.atTime(LocalTime.MAX));

        if (plans.isEmpty()) {
            return DailyStatisticsResult.of(member.getCreatedAt());
        }
        DailyStats dailyStats = statisticsService.calculateDailyStats(plans);

        return DailyStatisticsResult.from(member.getCreatedAt(),
                dailyStats,
                plans.stream().map(Plan::getFeedback).toList());
    }

    public WeeklyStatisticsResult calculateWeeklyStatistics(Long memberId, LocalDate startDate){
        Member member = memberApi.getMember(memberId);

        List<Plan> thisWeekPlans = planFacade.findPlans(member, startDate.atStartOfDay(), startDate.plusDays(6).atTime(LocalTime.MAX));

        if(thisWeekPlans.isEmpty()){
            return WeeklyStatisticsResult.of(member.getCreatedAt());
        }

        List<Plan> lastWeekPlans = planFacade.findPlans(member, startDate.minusWeeks(1).atStartOfDay(), startDate.minusWeeks(1).atTime(LocalTime.MAX));

        WeeklyTodoStats weeklyTodoStats = statisticsService.calculateWeeklyTodoStats(startDate, lastWeekPlans, thisWeekPlans);

        List<Feedback> thisWeeeFeedbacks = thisWeekPlans.stream().map(Plan::getFeedback).toList();
        WeeklyFeedbackStats weeklyFeedbackStats = statisticsService.calculateWeeklyFeedbackStats(thisWeeeFeedbacks);

        return WeeklyStatisticsResult.from(member.getCreatedAt(),
                weeklyTodoStats,
                weeklyFeedbackStats);
    }

}

