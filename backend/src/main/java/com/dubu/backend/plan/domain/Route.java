package com.dubu.backend.plan.domain;

import com.dubu.backend.global.domain.BaseTimeEntity;
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

    private Integer totalTime;

    private Double startX;

    private Double startY;

    private Double endX;

    private Double endY;
}