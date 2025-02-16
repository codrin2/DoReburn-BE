package com.dubu.backend.todo.repository;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.plan.domain.QPlan;
import com.dubu.backend.todo.dto.common.Cursor;
import com.dubu.backend.todo.dto.search.TodoSearchCond;
import com.dubu.backend.todo.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dubu.backend.plan.domain.QPath.*;
import static com.dubu.backend.plan.domain.QPlan.*;
import static com.dubu.backend.todo.entity.QCategory.*;
import static com.dubu.backend.todo.entity.QSchedule.*;
import static com.dubu.backend.todo.entity.QTodo.*;

@RequiredArgsConstructor
public class CustomTodoRepositoryImpl implements CustomTodoRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Todo> findTodosUsingSingleCursor(Long cursor, TodoSearchCond cond, Pageable pageable) {
        List<Todo> results = jpaQueryFactory.select(todo)
                .from(todo)
                .join(todo.category, category)
                .fetchJoin()
                .where(gtCursorId(cursor), eqMember(cond.member()), eqType(cond.type()))
                .limit(pageable.getPageSize() + 1)
                .fetch();
        boolean hasNext = false;

        if(results.size() > pageable.getPageSize()){
            results.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Slice<Todo> findTodosUsingCompositeCursor(Cursor cursor, TodoSearchCond cond, Pageable pageable) {
        List<Todo> results  = jpaQueryFactory.select(todo)
                .from(todo)
                .join(todo.category, category)
                .fetchJoin()
                .where(cursor(cursor), eqMember(cond.member()), eqType(cond.type()), inCategories(cond.categories()), inDifficulties(cond.difficulties()))
                .orderBy(todo.category.id.asc(), todo.difficulty.asc(), todo.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;

        if(results.size() > pageable.getPageSize()){
            results.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Map<String, Long> findTodoCountGroupByCategoryForStopMembers(List<Member> members, LocalDate date){
        QSchedule scheduleSub = new QSchedule("scheduleSub");

        List<Tuple> result = jpaQueryFactory.select(todo.category.name, todo.count())
                .from(todo)
                .where(todo.schedule.in(
                        JPAExpressions.select(schedule)
                                .from(schedule)
                                .where(Expressions.list(schedule.member.id, schedule.date).in(
                                        JPAExpressions.select(scheduleSub.member.id, scheduleSub.date.max())
                                                .from(scheduleSub)
                                                .where(scheduleSub.member.in(members)
                                                        .and(scheduleSub.date.loe(date)))
                                                .groupBy(scheduleSub.member)
                                )))
                ).groupBy(todo.category)
                .fetch();

        return result.stream()
                .collect(Collectors.toMap(
                        t -> t.get(todo.category.name),
                        t -> {
                            Long count = t.get(todo.count());
                            return count == null ? 0: count;
                        }
                ));
    }

    @Override
    public Map<String, Long> findTodoCountGroupByCategoryForMoveOrFeedbackMembers(List<Member> members){
        QPlan planSub = new QPlan("planSub");

        List<Tuple> result = jpaQueryFactory.select(todo.category.name, todo.count())
                .from(todo)
                .where(todo.path.in(
                        JPAExpressions.select(path)
                                .from(path)
                                .where(path.plan.in(
                                        JPAExpressions.select(plan)
                                                .from(plan)
                                                .where(Expressions.list(plan.member.id, plan.createdAt).in(
                                                        JPAExpressions.select(planSub.member.id, planSub.createdAt.max())
                                                                .from(planSub)
                                                                .where(planSub.member.in(members))
                                                                .groupBy(planSub.member)
                                                ))
                                ))
                )).groupBy(todo.category)
                .fetch();

        return result.stream()
                .collect(Collectors.toMap(
                        t -> t.get(todo.category.name),
                        t -> {
                            Long count = t.get(todo.count());
                            return count == null ? 0: count;
                        }
                ));
    }

    private BooleanExpression cursor(Cursor cursor){
        Long categoryId = cursor.cursorCategoryId();
        String difficulty = cursor.cursorDifficulty();
        Long todoId = cursor.cursorTodoId();

        if(categoryId == null || difficulty == null || todoId == null){
            return null;
        }

        BooleanExpression cond1 = todo.category.id.gt(categoryId);
        BooleanExpression cond2 = todo.category.id.eq(categoryId).and(todo.difficulty.gt(TodoDifficulty.valueOf(difficulty)));
        BooleanExpression cond3 = todo.category.id.eq(categoryId).and(todo.difficulty.eq((TodoDifficulty.valueOf(difficulty))).and(todo.id.gt(todoId)));

        return cond1.or(cond2).or(cond3);
    }

    private BooleanExpression gtCursorId(Long cursorId){
        if(cursorId != null)
            return todo.id.gt(cursorId);
        return null;
    }

    private BooleanExpression eqMember(Member member){
        if(member != null)
            return todo.member.eq(member);
        return null;
    }

    private BooleanExpression eqType(TodoType type){
        if(type != null)
            return todo.type.eq(type);
        return null;
    }

    private BooleanExpression inCategories(List<Category> categories){
        if(categories != null)
            return todo.category.in(categories);
        return null;
    }

    private BooleanExpression inDifficulties(List<TodoDifficulty> difficulties){
        if(difficulties != null)
            return todo.difficulty.in(difficulties);
        return null;
    }
}
