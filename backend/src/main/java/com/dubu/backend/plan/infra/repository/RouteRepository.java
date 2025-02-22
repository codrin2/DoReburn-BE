package com.dubu.backend.plan.infra.repository;

import com.dubu.backend.plan.domain.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {
    @Query("""
        select r
        from Route r
            left join fetch r.paths p
        where r.startX = :startX
          and r.startY = :startY
          and r.endX = :endX
          and r.endY = :endY
    """)
    Optional<Route> findRouteWithPathsByCoordinates(
            @Param("startX") Double startX,
            @Param("startY") Double startY,
            @Param("endX") Double endX,
            @Param("endY") Double endY
    );
}