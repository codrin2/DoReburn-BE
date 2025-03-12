package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.plan.domain.Plan;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.dubu.backend.plan.domain.QSubPath.subPath;
import static com.dubu.backend.todo.domain.QTodo.*;

@RequiredArgsConstructor
public class CustomPathRepositoryImpl implements CustomPathRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<SubPath> findByPlanWithTodosOrderByPathOrder(Plan plan) {
        return queryFactory
                .selectFrom(subPath)
                .leftJoin(subPath.todos, todo).fetchJoin()
                .where(subPath.plan.eq(plan))
                .orderBy(subPath.pathOrder.asc())
                .fetch();
    }
}