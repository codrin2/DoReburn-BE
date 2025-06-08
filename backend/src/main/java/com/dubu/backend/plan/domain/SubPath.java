package com.dubu.backend.plan.domain;

import com.dubu.backend.core.domain.BaseTimeEntity;
import com.dubu.backend.plan.domain.enums.TrafficType;
import com.dubu.backend.plan.api.request.PlanCreateRequest;
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
public class SubPath extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_path_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "path_id")
    private Path path;

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

    @Transient
    private List<Todo> todos = new ArrayList<>();

    public static SubPath createPath(Plan plan, Path path, PlanCreateRequest.Path pathRequest, int pathOrder) {
        return SubPath.builder()
                .plan(plan)
                .path(path)
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
    public void assignTodos(List<Todo> todos){
        if(todos != null && !todos.isEmpty()) this.todos = List.copyOf(todos);
    }

}