package com.dubu.backend.plan.domain;

import com.dubu.backend.core.domain.BaseTimeEntity;
import com.dubu.backend.plan.domain.enums.TrafficType;
import com.dubu.backend.plan.dto.request.PlanCreateRequest;
import com.dubu.backend.todo.domain.Todo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"plan_id", "pathOrder"}),
        @UniqueConstraint(columnNames = {"route_id", "pathOrder"})
})
public class Path extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "path_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

//    @BatchSize(size = 100)
    @Builder.Default
    @OneToMany(mappedBy = "path", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Todo> todos = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrafficType trafficType;

    @Column(columnDefinition = "SMALLINT")
    private Integer subwayCode;

    @Column(length = 20)
    private String busNumber;

    @Column(columnDefinition = "SMALLINT")
    private Integer busType;

    @Column(nullable = false, length = 20)
    private String startName;

    @Column(nullable = false, length = 20)
    private String endName;

    @Column(nullable = false, columnDefinition = "SMALLINT")
    private Integer sectionTime;

    @Column(nullable = false, columnDefinition = "SMALLINT")
    private Integer pathOrder;

    public static Path createPath(Plan plan, Route route, PlanCreateRequest.Path pathRequest, int pathOrder) {
        return Path.builder()
                .plan(plan)
                .route(route)
                .trafficType(TrafficType.from(pathRequest.trafficType()))
                .subwayCode(pathRequest.subwayCode())
                .busNumber(pathRequest.busNumber())
                .busType(pathRequest.busType())
                .startName(pathRequest.startName())
                .endName(pathRequest.endName())
                .sectionTime(pathRequest.sectionTime())
                .pathOrder(pathOrder)
                .build();
    }
}