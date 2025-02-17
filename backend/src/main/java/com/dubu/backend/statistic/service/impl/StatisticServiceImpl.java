package com.dubu.backend.statistic.service.impl;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infra.repository.MemberRepository;
import com.dubu.backend.plan.domain.Feedback;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.infra.repository.PathRepository;
import com.dubu.backend.plan.infra.repository.PlanRepository;
import com.dubu.backend.statistic.dto.response.DayStatisticInfo;
import com.dubu.backend.statistic.dto.response.WeekStatisticInfo;
import com.dubu.backend.statistic.service.StatisticService;
import com.dubu.backend.statistic.service.collection.CategoryTodoStatistics;
import com.dubu.backend.statistic.service.collection.DateUsageTimeStatistics;
import com.dubu.backend.todo.entity.Category;
import com.dubu.backend.todo.entity.Todo;
import com.dubu.backend.todo.entity.TodoType;
import com.dubu.backend.todo.repository.CategoryRepository;
import com.dubu.backend.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticServiceImpl implements StatisticService {
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final PlanRepository planRepository;
    private final TodoRepository todoRepository;
    private final PathRepository pathRepository;

    @Override
    public DayStatisticInfo collectDayStatistic(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        List<Category> categories = categoryRepository.findAll();

        // 쿼리 최적 -> 쿼리 분리
        List<Plan> dayPlans = planRepository.findByMemberAndCreatedAtBetween(member, date.atStartOfDay(), date.plusWeeks(1).atTime(LocalTime.MAX));
        List<Path> dayPaths = pathRepository.findByPlansAndTypeAndIsCompleted(dayPlans, TodoType.DONE, true);

        if(dayPlans == null || dayPlans.isEmpty() || dayPaths == null || dayPaths.isEmpty()){
            return null;
        }

        return buildDailyStatisticInfo(categories, dayPlans, dayPaths);
    }

    @Override
    public WeekStatisticInfo collectWeekStatistic(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        List<Category> categories = categoryRepository.findAll();

        // 쿼리 최적 -> 쿼리 분리
        List<Plan> thisWeekPlans = planRepository.findByMemberAndCreatedAtBetween(member, date.atStartOfDay(), date.plusWeeks(1).atTime(LocalTime.MAX));
        List<Path> thisWeekPaths = pathRepository.findByPlansAndTypeAndIsCompleted(thisWeekPlans, TodoType.DONE, true);

        if(thisWeekPlans == null || thisWeekPlans.isEmpty()){
            return null;
        }

        List<Plan> lastWeekPlans = planRepository.findByMemberAndCreatedAtBetween(member, date.minusWeeks(1).atStartOfDay(), date.minusDays(1).atTime(LocalTime.MAX));

        return buildWeekStatisticInfo(date, categories, thisWeekPlans, thisWeekPaths, lastWeekPlans);
    }


    private DayStatisticInfo buildDailyStatisticInfo(List<Category> categories, List<Plan> plans, List<Path> paths) {
        int totalMoveTime = 0;
        int totalUsageTime = 0;
        List<Feedback> feedbacks = new ArrayList<>();
        CategoryTodoStatistics categoryTodoStatistics = new CategoryTodoStatistics(categories);

        for(Plan plan: plans){
            totalMoveTime += plan.getTotalTime();
            feedbacks.add(plan.getFeedback());
        }

        for(Path path: paths){
            for(Todo todo: path.getTodos()){
                categoryTodoStatistics.countDoneTodo(todo);
                totalUsageTime += todo.getSpentTime();
            }
        }

        return DayStatisticInfo.of(totalMoveTime, totalUsageTime, feedbacks, categoryTodoStatistics.getCategoryTodoTimeCount());
    }

    private WeekStatisticInfo buildWeekStatisticInfo(LocalDate startDate, List<Category> categories, List<Plan> thisWeekPlans, List<Path> thisWeekPaths, List<Plan> lastWeekPlans){
        DateUsageTimeStatistics dateUsageTimeStatistics = new DateUsageTimeStatistics(startDate);
        int totalMoveTime = 0;
        int totalUsageTime = 0;
        int totalTodoCount = 0;
        CategoryTodoStatistics categoryTodoStatistics = new CategoryTodoStatistics(categories);

        for(Plan plan: thisWeekPlans){
            dateUsageTimeStatistics.addUsageTimeAtDate(plan.getCreatedAt().toLocalDate(), plan.getTotalTime());
            totalMoveTime += plan.getTotalTime();
        }

        for(Path path: thisWeekPaths){
            for (Todo todo : path.getTodos()) {
                totalTodoCount += 1;
                categoryTodoStatistics.recordDoneTodo(todo);
                totalUsageTime += todo.getSpentTime();
            }
        }


        int lastWeekDiff = totalUsageTime - calculateWeeklyUsageTime(lastWeekPlans);

        return WeekStatisticInfo.of(dateUsageTimeStatistics.getDateUsageTime(), totalTodoCount, lastWeekDiff, totalMoveTime, totalUsageTime, categoryTodoStatistics.getCategoryTodoTimeCount());
    }

    private int calculateWeeklyUsageTime(List<Plan> plans){
        return plans.stream().mapToInt(Plan::getTotalTime).sum();
    }
}