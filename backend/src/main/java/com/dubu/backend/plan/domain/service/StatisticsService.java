package com.dubu.backend.plan.domain.service;

import com.dubu.backend.plan.domain.Category;
import com.dubu.backend.plan.domain.Feedback;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.Todo;
import com.dubu.backend.plan.domain.enums.Mood;
import com.dubu.backend.plan.domain.vo.DailyStats;
import com.dubu.backend.plan.domain.vo.WeeklyFeedbackStats;
import com.dubu.backend.plan.domain.vo.WeeklyTodoStats;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Component
public class StatisticsService {

    public DailyStats calculateDailyStats(List<Plan> plans){
        int totalTodoCount = 0;
        Duration totalUsageTime = Duration.ZERO;
        Map<Category, Integer> categoryCountMap = new HashMap<>();

        for(Plan plan: plans){
            totalUsageTime = totalUsageTime.plusMinutes(plan.getTotalTime());

            List<Todo> todos = plan.getTodos();
            totalTodoCount += todos.size();
            for(Todo todo: todos){
                categoryCountMap.merge(todo.getCategory(), 1, Integer::sum);
            }
        }
        return DailyStats.of(totalUsageTime, totalTodoCount, categoryCountMap);
    }

    public WeeklyTodoStats calculateWeeklyTodoStats(LocalDate startDate, List<Plan> lastWeekPlans, List<Plan> thisWeekPlans){
        int totalTodoCount = 0;
        Duration totalUsageTime = Duration.ZERO;
        Map<LocalDate, Duration> dayUsageTimeMap = IntStream.range(0, 7).boxed().collect(HashMap::new, (m, i) -> m.put(startDate.plusDays(i), Duration.ZERO), HashMap::putAll);
        Map<Category, Integer> categoryCountMap = new HashMap<>();
        Map<Category, Duration> categoryUsageTimeMap = new HashMap<>();

        for(Plan plan: thisWeekPlans){
            totalUsageTime = totalUsageTime.plusMinutes(plan.getTotalTime());

            dayUsageTimeMap.merge(plan.getCreatedAt().toLocalDate(), Duration.ofMinutes(plan.getTotalTime()), Duration::plus);

            List<Todo> todos = plan.getTodos();
            totalTodoCount += todos.size();
            for(Todo todo: todos){
                categoryCountMap.merge(todo.getCategory(), 1, Integer::sum);
                categoryUsageTimeMap.merge(todo.getCategory(), Duration.ofMinutes(plan.getTotalTime()), Duration::plus);
            }
        }

        Duration lastWeekTotalUsageTime = Duration.ZERO;
        for(Plan plan: lastWeekPlans){
            lastWeekTotalUsageTime = lastWeekTotalUsageTime.plusMinutes(plan.getTotalTime());
        }

        return WeeklyTodoStats.of(dayUsageTimeMap, totalTodoCount, totalUsageTime, totalUsageTime.minus(lastWeekTotalUsageTime), categoryCountMap, categoryUsageTimeMap);
    }

    public WeeklyFeedbackStats calculateWeeklyFeedbackStats(List<Feedback> feedbacks) {
        Map<Mood, Integer> moodCountMap = IntStream.range(0, 3).boxed().collect(HashMap::new, (m, i) -> m.put(Mood.values()[i], 0), HashMap::putAll);

        feedbacks.forEach(f -> moodCountMap.merge(f.getMood(), 1, Integer::sum));

        return WeeklyFeedbackStats.of(moodCountMap);
    }


}
