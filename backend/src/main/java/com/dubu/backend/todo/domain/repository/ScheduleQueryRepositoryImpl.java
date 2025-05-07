package com.dubu.backend.todo.domain.repository;

import com.dubu.backend.todo.domain.Schedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

import static com.dubu.backend.todo.domain.QSchedule.*;

@RequiredArgsConstructor
public class ScheduleQueryRepositoryImpl implements ScheduleQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Schedule> findLatestSchedule(Long memberId, LocalDate date) {
        return Optional.ofNullable(queryFactory.selectFrom(schedule).fetchJoin()
                .fetchFirst());
    }
}
