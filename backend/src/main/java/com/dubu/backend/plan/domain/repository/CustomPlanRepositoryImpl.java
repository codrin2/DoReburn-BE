package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.plan.domain.*;
import com.dubu.backend.plan.domain.repository.dto.PlanSearchCond;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dubu.backend.plan.domain.QFeedback.*;
import static com.dubu.backend.plan.domain.QPlan.*;
import static com.dubu.backend.plan.domain.QSubPath.*;
import static com.dubu.backend.plan.domain.QTodo.*;

@RequiredArgsConstructor
public class CustomPlanRepositoryImpl implements CustomPlanRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Plan> findPlans(Member member, PlanSearchCond cond) {
        List<Plan> plans = queryFactory.selectFrom(plan)
                .leftJoin(plan.subPaths, subPath)
                .fetchJoin()
                .leftJoin(plan.feedback, feedback)
                .fetchJoin()
                .where(planCond(member, cond))
                .fetch();

        List<SubPath> subPaths = plans.stream()
                .flatMap(p -> p.getSubPaths().stream())
                .toList();

        if(subPaths.isEmpty()) {return plans;}

        List<Todo> todos = queryFactory.selectFrom(todo)
                .leftJoin(todo.subPath, subPath)
                .fetchJoin()
                .where(subPathIn(subPath, subPaths), isCompletedEq(todo, cond.isTodoCompleted()))
                .fetch();

        Map<Long, List<Todo>> todosBySubPath = todos.stream()
                .collect(Collectors.groupingBy(t -> t.getSubPath().getId()));

        for(Plan plan: plans){
            plan.defineTodos(plan.getSubPaths().stream()
                            .flatMap(sp -> todosBySubPath.getOrDefault(sp.getId(), List.of()).stream())
                            .toList());
        }

        return plans;
    }
    private BooleanBuilder planCond(Member member, PlanSearchCond cond){
        BooleanBuilder builder = new BooleanBuilder();

        if(cond == null){
            return builder;
        }

        return builder
                .and(memberEq(member))
                .and(createdAtBetween(cond.startTime(), cond.endTime()))
                .and(isCompletedEq(cond.isPlanCompleted()));
    }

    private BooleanExpression memberEq(Member member){
        return member != null ? plan.member.eq(member) : null;
    }

    private BooleanExpression createdAtBetween(LocalDateTime startTime, LocalDateTime endTime){
        return startTime != null && endTime != null ? plan.createdAt.between(startTime, endTime) : null;
    }

    private BooleanExpression isCompletedEq(Boolean isCompleted){
        return isCompleted != null ? plan.isCompleted.eq(isCompleted): null;
    }

    private BooleanExpression isCompletedEq(QTodo todo, Boolean isCompleted){
        return isCompleted != null ? todo.isCompleted.eq(isCompleted) : null;
    }

    private BooleanExpression subPathIn(QSubPath subPath, List<SubPath> subPaths){
        return subPaths != null && !subPaths.isEmpty() ? subPath.in(subPaths) : null;
    }
}
