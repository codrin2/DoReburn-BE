package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.plan.domain.*;
import com.dubu.backend.plan.domain.repository.dto.PlanSearchCond;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.dubu.backend.plan.domain.QFeedback.*;
import static com.dubu.backend.plan.domain.QPlan.*;
import static com.dubu.backend.plan.domain.QSubPath.*;

@RequiredArgsConstructor
public class CustomPlanRepositoryImpl implements CustomPlanRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Plan> findPlans(PlanSearchCond cond) {
        List<Plan> plans = queryFactory.selectFrom(plan)
                .leftJoin(plan.subPaths, subPath)
                .fetchJoin()
                .leftJoin(plan.feedback, feedback)
                .fetchJoin()
                .where(planCond(cond))
                .fetch();

        List<SubPath> subPaths = plans.stream()
                .flatMap(p -> p.getSubPaths().stream())
                .toList();

        if(subPaths.isEmpty()) {return plans;}

        return plans;
    }

    @Override
    public List<Long> findCompletedRecentPlansId(List<Long> memberIds) {
        QPlan subPlan = new QPlan("subPlan");

        return queryFactory.select(plan.id).from(plan)
                .where(plan.memberId.in(memberIds), isCompletedEq(true), plan.createdAt.eq(
                        JPAExpressions.select(subPlan.createdAt.max())
                                .from(subPlan)
                                .where(subPlan.memberId.in(memberIds), isCompletedEq(true))
                ))
                .fetch();
    }

    private BooleanBuilder planCond(PlanSearchCond cond){
        BooleanBuilder builder = new BooleanBuilder();

        if(cond == null){
            return builder;
        }

        return builder
                .and(memberIdEq(cond.memberId()))
                .and(createdAtBetween(cond.startTime(), cond.endTime()))
                .and(isCompletedEq(cond.isPlanCompleted()));
    }

    private BooleanExpression memberIdEq(Long memberId){
        return memberId != null ? plan.memberId.eq(memberId) : null;
    }

    private BooleanExpression createdAtBetween(LocalDateTime startTime, LocalDateTime endTime){
        return startTime != null && endTime != null ? plan.createdAt.between(startTime, endTime) : null;
    }

    private BooleanExpression isCompletedEq(Boolean isCompleted){
        return isCompleted != null ? plan.isCompleted.eq(isCompleted): null;
    }

    private BooleanExpression subPathIn(QSubPath subPath, List<SubPath> subPaths){
        return subPaths != null && !subPaths.isEmpty() ? subPath.in(subPaths) : null;
    }
}
