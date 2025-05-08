package com.dubu.backend.plan.application;

import com.dubu.backend.plan.application.dto.DailyStatisticsResult;
import com.dubu.backend.plan.application.dto.WeeklyStatisticsResult;
import com.dubu.backend.plan.core.exception.MemberNotFoundException;
import com.dubu.backend.plan.domain.Feedback;
import com.dubu.backend.plan.domain.Member;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.repository.MemberRepository;
import com.dubu.backend.plan.domain.repository.PlanRepository;
import com.dubu.backend.plan.domain.repository.dto.PlanSearchCond;
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
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    private final StatisticsService statisticsService;

    public DailyStatisticsResult calculateDayStatistics(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        List<Plan> plans = planRepository.findPlans(member,
                PlanSearchCond.of(true, true, date.atStartOfDay(), date.atTime(LocalTime.MAX)));

        if (plans.isEmpty()) {
            return DailyStatisticsResult.of(member.getCreatedAt().toLocalDate());
        }
        DailyStats dailyStats = statisticsService.calculateDailyStats(plans);

        return DailyStatisticsResult.from(member.getCreatedAt().toLocalDate(),
                dailyStats,
                plans.stream().map(Plan::getFeedback).toList());
    }

    public WeeklyStatisticsResult calculateWeeklyStatistics(Long memberId, LocalDate startDate){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        List<Plan> thisWeekPlans = planRepository.findPlans(member,
                PlanSearchCond.of(true,
                        true,
                        startDate.atStartOfDay(),
                        startDate.plusDays(6).atTime(LocalTime.MAX)));

        if(thisWeekPlans.isEmpty()){
            return WeeklyStatisticsResult.of(member.getCreatedAt().toLocalDate());
        }

        List<Plan> lastWeekPlans = planRepository.findPlans(member,
                PlanSearchCond.of(true,
                        true,
                        startDate.minusWeeks(1).atStartOfDay(),
                        startDate.minusDays(1).atTime(LocalTime.MAX)));

        WeeklyTodoStats weeklyTodoStats = statisticsService.calculateWeeklyTodoStats(startDate, lastWeekPlans, thisWeekPlans);

        List<Feedback> thisWeeeFeedbacks = thisWeekPlans.stream().map(Plan::getFeedback).toList();
        WeeklyFeedbackStats weeklyFeedbackStats = statisticsService.calculateWeeklyFeedbackStats(thisWeeeFeedbacks);

        return WeeklyStatisticsResult.from(member.getCreatedAt().toLocalDate(),
                weeklyTodoStats,
                weeklyFeedbackStats);
    }

}

