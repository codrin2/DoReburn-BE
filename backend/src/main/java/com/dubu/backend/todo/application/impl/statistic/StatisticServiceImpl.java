package com.dubu.backend.todo.application.impl.statistic;

import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.domain.Feedback;
import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.enums.TrafficType;
import com.dubu.backend.plan.domain.repository.SubPathRepository;
import com.dubu.backend.plan.domain.repository.PlanRepository;
import com.dubu.backend.todo.dto.response.DayStatisticInfo;
import com.dubu.backend.todo.dto.response.WeekStatisticInfo;
import com.dubu.backend.todo.application.StatisticService;
import com.dubu.backend.todo.application.collection.statistic.CategoryTodoStatistics;
import com.dubu.backend.todo.application.collection.statistic.DateAvailableTimeStatistics;
import com.dubu.backend.todo.application.collection.statistic.MoodCountCollection;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
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
    private final SubPathRepository subPathRepository;

    @Override
    public DayStatisticInfo collectDayStatistic(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        List<Category> categories = categoryRepository.findAll();

        // 쿼리 최적 -> 쿼리 분리
        List<Plan> dayPlans = planRepository.findByMemberAndCreatedAtBetween(member, date.atStartOfDay(), date.atTime(LocalTime.MAX));
        List<SubPath> daySubPaths = subPathRepository.findByPlansAndTypeAndIsCompleted(dayPlans, TodoType.DONE, true);

        if(dayPlans == null || dayPlans.isEmpty()){
            return DayStatisticInfo.of(member.getCreatedAt().toLocalDate());
        }

        return buildDailyStatisticInfo(member.getCreatedAt().toLocalDate(), categories, dayPlans, daySubPaths);
    }

    @Override
    public WeekStatisticInfo collectWeekStatistic(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        List<Category> categories = categoryRepository.findAll();

        // 쿼리 최적 -> 쿼리 분리
        List<Plan> thisWeekPlans = planRepository.findWithPathsByMemberAndCreatedAtBetween(member, date.atStartOfDay(), date.plusDays(6).atTime(LocalTime.MAX));
//        List<Long> pathIdsOfWeekPlans = planRepository.findPathIdsWithPathsByMemberAndCreatedAtBetween(member, date.atStartOfDay(), date.plusWeeks(1).atTime(LocalTime.MAX));
        List<Long> pathIdsOfWeekPlans = subPathRepository.findPathIdsByMemberAndCreatedAtBetween(member, date.atStartOfDay(), date.plusDays(6).atTime(LocalTime.MAX));
        List<Todo> completedTodos = todoRepository.findByPathIdsAndTypeAndIsCompleted(pathIdsOfWeekPlans, TodoType.DONE);
//        List<Path> thisWeekPaths = pathRepository.findByPlansAndTypeAndIsCompleted(thisWeekPlans, TodoType.DONE, true);

        if(thisWeekPlans == null || thisWeekPlans.isEmpty()){
            return WeekStatisticInfo.of(member.getCreatedAt().toLocalDate());
        }

        List<Plan> lastWeekPlans = planRepository.findWithPathsByMemberAndCreatedAtBetween(member, date.minusWeeks(1).atStartOfDay(), date.minusDays(1).atTime(LocalTime.MAX));

        return buildWeekStatisticInfoUsingPathIds(member.getCreatedAt().toLocalDate(), date, categories, thisWeekPlans, completedTodos, lastWeekPlans);
    }


    private DayStatisticInfo buildDailyStatisticInfo(LocalDate memberCreateDate, List<Category> categories, List<Plan> plans, List<SubPath> subPaths) {
        int totalAvailableTime = 0;
        int totalTodoCount = 0;
        List<Feedback> feedbacks = new ArrayList<>();
        CategoryTodoStatistics categoryTodoStatistics = new CategoryTodoStatistics(categories);

        for(Plan plan: plans){
            totalAvailableTime += plan.getTotalTime();
            feedbacks.add(plan.getFeedback());
        }

        for(SubPath subPath : subPaths){
            if(subPath.getTrafficType() != TrafficType.WALK){
                for(Todo todo: subPath.getTodos()){
                    categoryTodoStatistics.countDoneTodo(todo);
                    totalTodoCount++;
                }
            }
        }

        return DayStatisticInfo.of(memberCreateDate, totalAvailableTime, totalTodoCount, feedbacks, categoryTodoStatistics.getCategoryTodoTimeCount());
    }

    private WeekStatisticInfo buildWeekStatisticInfo(LocalDate memberCreateDate, LocalDate startDate, List<Category> categories, List<Plan> thisWeekPlans, List<Plan> lastWeekPlans){
        DateAvailableTimeStatistics dateAvailableTimeStatistics = new DateAvailableTimeStatistics(startDate);
        int totalAvailableTime = 0;
        int totalTodoCount = 0;
        CategoryTodoStatistics categoryTodoStatistics = new CategoryTodoStatistics(categories);
        MoodCountCollection moodCountCollection = new MoodCountCollection();

        for(Plan plan: thisWeekPlans){
            LocalDate date = plan.getCreatedAt().toLocalDate();
            totalAvailableTime += plan.getTotalTime();
            moodCountCollection.addMood(plan.getFeedback().getMood());

            for (SubPath subPath : plan.getSubPaths()) {
                dateAvailableTimeStatistics.addUsageTimeAtDate(date, subPath.getSectionTime());
                for (Todo todo : subPath.getTodos()) {
                    totalTodoCount += 1;
                    categoryTodoStatistics.recordDoneTodo(todo);
                }
            }
        }
        // 저번 주와의 활용 가능 시간 차이
        int lastWeekDiff = totalAvailableTime - calculateWeeklyUsageTime(lastWeekPlans);

        return WeekStatisticInfo.of(memberCreateDate, dateAvailableTimeStatistics.getDateUsageTime(), totalTodoCount, lastWeekDiff, totalAvailableTime, moodCountCollection.getMoodCount(), categoryTodoStatistics.getCategoryTodoTimeCount());
    }

    private WeekStatisticInfo buildWeekStatisticInfoUsingPathIds(LocalDate memberCreateDate, LocalDate startDate, List<Category> categories, List<Plan> thisWeekPlans, List<Todo> completedTodos, List<Plan> lastWeekPlans) {
        DateAvailableTimeStatistics dateAvailableTimeStatistics = new DateAvailableTimeStatistics(startDate);
        int totalAvailableTime = 0;
        int totalTodoCount = 0;
        CategoryTodoStatistics categoryTodoStatistics = new CategoryTodoStatistics(categories);
        MoodCountCollection moodCountCollection = new MoodCountCollection();

        for(Plan plan: thisWeekPlans){
            LocalDate date = plan.getCreatedAt().toLocalDate();
            moodCountCollection.addMood(plan.getFeedback().getMood());
            totalAvailableTime += plan.getTotalTime();
            dateAvailableTimeStatistics.addUsageTimeAtDate(date, plan.getTotalTime());
        }

        // 이번 주 카테고리별 통계 집계
        for(Todo todo: completedTodos){
            totalTodoCount += 1;
            categoryTodoStatistics.recordDoneTodo(todo);
        }

        // 저번 주와의 활용 가능한 시간 차이
        int lastWeekDiff = totalAvailableTime - calculateWeeklyUsageTime(lastWeekPlans);

        return WeekStatisticInfo.of(memberCreateDate, dateAvailableTimeStatistics.getDateUsageTime(), totalTodoCount, lastWeekDiff, totalAvailableTime, moodCountCollection.getMoodCount(), categoryTodoStatistics.getCategoryTodoTimeCount());
    }

    // 활용 가능한 시간 계산
    private int calculateWeeklyUsageTime(List<Plan> plans){
        return plans.stream()
                .mapToInt(Plan::getTotalTime)
                .sum();
    }
}