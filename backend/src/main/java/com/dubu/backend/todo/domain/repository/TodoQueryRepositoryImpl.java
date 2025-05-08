package com.dubu.backend.todo.domain.repository;

import com.dubu.backend.todo.core.dto.TodoCursor;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.QTodo;
import com.dubu.backend.todo.domain.dto.CategoryInfo;
import com.dubu.backend.todo.domain.dto.TodoChildExistenceInfo;
import com.dubu.backend.todo.domain.dto.TodoInfo;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.domain.repository.dto.TodoChildExistenceCond;
import com.dubu.backend.todo.domain.repository.dto.TodoSearchCond;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.dubu.backend.todo.domain.QTodo.*;

@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findTodoIds(TodoSearchCond cond) {
        return queryFactory.select(todo.id)
                .from(todo)
                .where(allCond(cond))
                .fetch();
    }

    @Override
    public List<TodoInfo> findTodos(TodoSearchCond cond) {
        return queryFactory.select(Projections.constructor(TodoInfo.class, todo.id, todo.title, Projections.constructor(CategoryInfo.class, todo.category.id, todo.category.name), todo.difficulty))
                .from(todo)
                .where(allCond(cond))
                .fetch();
    }

    @Override
    public Slice<TodoInfo> findTodosPaged(TodoCursor cursor, TodoSearchCond cond, Pageable pageable) {
        List<TodoInfo> todoInfos = queryFactory.select(Projections.constructor(TodoInfo.class, todo.id, todo.title, Projections.constructor(CategoryInfo.class, todo.category.id, todo.category.name), todo.difficulty, todo.memo))
                .from(todo)
                .where(cursorCond(cond.type(), cursor), allCond(cond))
                .orderBy(orderSpecifiers(cond.type()))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = todoInfos.size() > pageable.getPageSize();

        if(hasNext){
            todoInfos.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(todoInfos, pageable, hasNext);
    }


    @Override
    public List<TodoChildExistenceInfo> findTodoChildExistence(List<Long> todoIds, TodoChildExistenceCond cond) {
        QTodo parent = todo;
        QTodo child = new QTodo("child");

        BooleanExpression hasChild = JPAExpressions.selectOne()
                .from(child)
                .where(childExistenceCond(child, cond), child.parentInfo.parentId.eq(parent.id).and(child.parentInfo.parentVersion.eq(parent.version)))
                .exists();

        return queryFactory.select(Projections.constructor(TodoChildExistenceInfo.class, parent.id, hasChild))
                .from(parent)
                .where(parent.id.in(todoIds))
                .fetch();
    }

    private BooleanBuilder cursorCond(TodoType type, TodoCursor cursor){
        BooleanBuilder builder = new BooleanBuilder();

        if(cursor == null)
            return builder;

        return switch(type){
            case FAVORITE -> builder.and(idGt(cursor.todoId()));
            case RECOMMEND -> builder.and(categoryIdGt(cursor.categoryId()))
                        .or(categoryIdEq(cursor.categoryId()).and(difficultyGt(cursor.difficulty())))
                        .or(categoryIdEq(cursor.categoryId()).and(difficultyEq(cursor.difficulty())).and(idGt(cursor.todoId())));
            default -> builder;
        };
    }

    private BooleanBuilder childExistenceCond(QTodo child, TodoChildExistenceCond cond){
        BooleanBuilder builder = new BooleanBuilder();

        if(cond == null){
            return builder;
        }

        return builder.and(memberIdEq(child, cond.memberId()))
                .and(typeEq(child, cond.type()))
                .and(scheduleIdEq(child, cond.scheduleId()))
                .and(subPathIdEq(child, cond.subPathId()));
    }

    private BooleanBuilder allCond(TodoSearchCond cond){
        BooleanBuilder builder = new BooleanBuilder();

        return builder
                .and(memberIdEq(cond.memberId()))
                .and(typeEq(cond.type()))
                .and(categoryIn(cond.categories()))
                .and(difficultyIn(cond.difficulties()));
    }

    private BooleanExpression idGt(Long todoId){ return todoId != null ? todo.id.gt(todoId) : null; }
    private BooleanExpression memberIdEq(Long memberId) {
        return memberId != null ? todo.memberId.eq(memberId) : null;
    }
    private BooleanExpression memberIdEq(QTodo todo, Long memberId) { return memberId != null? todo.memberId.eq(memberId): null; }


    private BooleanExpression categoryIdGt(Long categoryId){ return categoryId != null ? todo.category.id.gt(categoryId) : null; }
    private BooleanExpression categoryIdEq(Long categoryId){ return categoryId != null ? todo.category.id.eq(categoryId) : null; }
    private BooleanExpression categoryIn(List<Category> categories) {
        return categories != null && !categories.isEmpty() ? todo.category.in(categories) : null;
    }

    private BooleanExpression scheduleIdEq(QTodo todo, Long scheduleId){ return scheduleId != null ? todo.scheduleId.eq(scheduleId) : null; }

    private BooleanExpression subPathIdEq(QTodo todo, Long subPathId){ return subPathId != null ? todo.subPathId.eq(subPathId) : null; }


    private BooleanExpression typeEq(TodoType type){
        return type != null ? todo.type.eq(type) : null;
    }
    private BooleanExpression typeEq(QTodo todo, TodoType type){ return type != null ? todo.type.eq(type) : null; }


    private BooleanExpression difficultyGt(TodoDifficulty difficulty){ return difficulty != null ? todo.difficulty.gt(difficulty) : null; }
    private BooleanExpression difficultyEq(TodoDifficulty difficulty) { return difficulty != null ? todo.difficulty.eq(difficulty) : null; }
    private BooleanExpression difficultyIn(List<TodoDifficulty> difficulties){
        return difficulties != null && !difficulties.isEmpty() ? todo.difficulty.in(difficulties) : null;
    }

    private OrderSpecifier<?>[] orderSpecifiers(TodoType requestType){
        return switch(requestType){
            case FAVORITE -> new OrderSpecifier<?>[]{todo.id.asc()};
            case RECOMMEND -> new OrderSpecifier<?>[]{
                    todo.category.id.asc(),
                    todo.difficulty.asc(),
                    todo.id.asc()
            };
            default -> new OrderSpecifier<?>[]{};
        };
    }
}
