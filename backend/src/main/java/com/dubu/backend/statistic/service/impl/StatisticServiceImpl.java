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
import com.dubu.backend.statistic.service.collection.PathIdDateCollection;
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
        List<Plan> thisWeekPlans = planRepository.findWithPathsByMemberAndCreatedAtBetween(member, date.atStartOfDay(), date.plusWeeks(1).atTime(LocalTime.MAX));
//        List<Long> pathIdsOfWeekPlans = planRepository.findPathIdsWithPathsByMemberAndCreatedAtBetween(member, date.atStartOfDay(), date.plusWeeks(1).atTime(LocalTime.MAX));
        List<Long> pathIdsOfWeekPlans = pathRepository.findPathIdsByMemberAndCreatedAtBetween(member, date.atStartOfDay(), date.plusWeeks(1).atTime(LocalTime.MAX));
        List<Todo> completedTodos = todoRepository.findByPathIdsAndTypeAndIsCompleted(pathIdsOfWeekPlans, TodoType.DONE);
//        List<Path> thisWeekPaths = pathRepository.findByPlansAndTypeAndIsCompleted(thisWeekPlans, TodoType.DONE, true);

        if(thisWeekPlans == null || thisWeekPlans.isEmpty() || pathIdsOfWeekPlans == null || pathIdsOfWeekPlans.isEmpty() || completedTodos == null || completedTodos.isEmpty()){
            return null;
        }

        List<Plan> lastWeekPlans = planRepository.findByMemberAndCreatedAtBetween(member, date.minusWeeks(1).atStartOfDay(), date.minusDays(1).atTime(LocalTime.MAX));
        List<Path> lastWeekPaths = pathRepository.findByPlansAndTypeAndIsCompleted(lastWeekPlans, TodoType.DONE, true);

        return buildWeekStatisticInfoUsingPathIds(date, categories, thisWeekPlans, completedTodos, lastWeekPaths);
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

    private WeekStatisticInfo buildWeekStatisticInfo(LocalDate startDate, List<Category> categories, List<Plan> thisWeekPlans, List<Path> lastWeekPaths){
        DateUsageTimeStatistics dateUsageTimeStatistics = new DateUsageTimeStatistics(startDate);
        int totalMoveTime = 0;
        int totalUsageTime = 0;
        int totalTodoCount = 0;
        CategoryTodoStatistics categoryTodoStatistics = new CategoryTodoStatistics(categories);

        for(Plan plan: thisWeekPlans){
            LocalDate date = plan.getCreatedAt().toLocalDate();
            totalMoveTime += plan.getTotalTime();

            for (Path path : plan.getPaths()) {
                for (Todo todo : path.getTodos()) {
                    dateUsageTimeStatistics.addUsageTimeAtDate(date, todo.getSpentTime());
                    totalTodoCount += 1;
                    totalUsageTime += todo.getSpentTime();
                    categoryTodoStatistics.recordDoneTodo(todo);
                }
            }
        }
        // 저번 주와의 활용 시간 차이
        int lastWeekDiff = totalUsageTime - calculateWeeklyUsageTime(lastWeekPaths);

        return WeekStatisticInfo.of(dateUsageTimeStatistics.getDateUsageTime(), totalTodoCount, lastWeekDiff, totalMoveTime, totalUsageTime, categoryTodoStatistics.getCategoryTodoTimeCount());
    }

    private WeekStatisticInfo buildWeekStatisticInfoUsingPathIds(LocalDate startDate, List<Category> categories, List<Plan> thisWeekPlans, List<Todo> completedTodos, List<Path> lastWeekPaths) {
        DateUsageTimeStatistics dateUsageTimeStatistics = new DateUsageTimeStatistics(startDate);
        int totalMoveTime = 0;
        int totalUsageTime = 0;
        int totalTodoCount = 0;
        CategoryTodoStatistics categoryTodoStatistics = new CategoryTodoStatistics(categories);

        PathIdDateCollection pathIdDateCollection = new PathIdDateCollection();

        for(Plan plan: thisWeekPlans){
            LocalDate date = plan.getCreatedAt().toLocalDate();
            totalMoveTime += plan.getTotalTime();

            // pathId 와 날짜 매핑
            for (Path path : plan.getPaths()) {
                if(path.getId() != null){
                    pathIdDateCollection.putLocalDate(path.getId(), date);
                }
            }
        }

        // 이번 주의 완료된 할 일(completedTodos) 처리: 각 Todo의 spentTime, 날짜별/카테고리별 집계
        for(Todo todo: completedTodos){
            Path path = todo.getPath();
            if(path == null) continue;

            LocalDate date = pathIdDateCollection.getLocalDate(path.getId());
            if(date == null) continue;

            // 날짜별 집계
            dateUsageTimeStatistics.addUsageTimeAtDate(date, todo.getSpentTime());
            totalTodoCount += 1;
            totalUsageTime += todo.getSpentTime();
            categoryTodoStatistics.recordDoneTodo(todo);
        }

        // 저번 주와의 활용 시간 차이
        int lastWeekDiff = totalUsageTime - calculateWeeklyUsageTime(lastWeekPaths);

        return WeekStatisticInfo.of(dateUsageTimeStatistics.getDateUsageTime(), totalTodoCount, lastWeekDiff, totalMoveTime, totalUsageTime, categoryTodoStatistics.getCategoryTodoTimeCount());
    }


    private int calculateWeeklyUsageTime(List<Path> paths){
        return paths.stream()
                .flatMap(path -> path.getTodos().stream())
                .mapToInt(Todo::getSpentTime)
                .sum();
    }
}