package com.dubu.backend.plan.api.response;

import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.enums.TrafficType;
import com.dubu.backend.todo.domain.Todo;

import java.time.LocalDateTime;
import java.util.List;

public record PlanRecentResponse(
        Long planId,
        Integer totalSectionTime,
        LocalDateTime createdAt,
        List<PlanRecentResponse.PlanPathResponse> paths
) {
    public static PlanRecentResponse of(Plan plan, List<SubPath> subPaths) {
        return new PlanRecentResponse(
                plan.getId(),
                plan.getTotalTime(),
                plan.getCreatedAt(),
                subPaths.stream()
                        .filter(path -> path.getTrafficType() != TrafficType.WALK)
                        .map(PlanPathResponse::from)
                        .toList()
        );
    }

    public record PlanPathResponse(
            Long pathId,
            String trafficType,
            Integer sectionTime,
            Integer subwayCode,
            String busNumber,
            Integer busType,
            String startName,
            String endName,
            List<PlanRecentResponse.PathTodoResponse> todos
    ) {
        public static PlanPathResponse from(SubPath subPath) {
            return new PlanPathResponse(
                    subPath.getId(),
                    subPath.getTrafficType().name(),
                    subPath.getSectionTime(),
                    subPath.getSubwayCode(),
                    subPath.getBusNumber(),
                    subPath.getBusType(),
                    subPath.getStartName(),
                    subPath.getEndName(),
                    subPath.getTodos().stream().map(PathTodoResponse::from).toList()
            );
        }
    }

    public record PathTodoResponse(
            Long todoId,
            boolean isDone,
            String title,
            String category,
            String difficulty,
            String memo
    ) {
        public static PathTodoResponse from(Todo todo) {
            return new PathTodoResponse(
                    todo.getId(),
                    todo.getIsCompleted() == Boolean.TRUE,
                    todo.getTitle(),
                    todo.getCategory().getName(),
                    todo.getDifficulty().name(),
                    todo.getMemo()
            );
        }
    }
}