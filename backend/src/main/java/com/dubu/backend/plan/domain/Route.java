package com.dubu.backend.plan.domain;

import com.dubu.backend.core.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Route extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "route", cascade = CascadeType.REMOVE)
    private List<Path> paths = new ArrayList<>();

    @Column(nullable = false)
    private Integer totalTime;

    @Column(nullable = false)
    private Double startX;

    @Column(nullable = false)
    private Double startY;

    @Column(nullable = false)
    private Double endX;

    @Column(nullable = false)
    private Double endY;

    public static Route createRoute(Double startX, Double startY, Double endX, Double endY, Integer totalTime) {
        return Route.builder()
                .startX(startX)
                .startY(startY)
                .endX(endX)
                .endY(endY)
                .totalTime(totalTime)
                .build();
    }
}