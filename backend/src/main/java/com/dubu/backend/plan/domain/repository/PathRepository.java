package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.plan.domain.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PathRepository extends JpaRepository<Path, Long> {
    @Query("""
        select r
        from Path r
            left join fetch r.subPaths
        where r.startX = :startX
          and r.startY = :startY
          and r.endX = :endX
          and r.endY = :endY
    """)
    List<Path> findAllWithPathsByCoordinates(
            @Param("startX") Double startX,
            @Param("startY") Double startY,
            @Param("endX") Double endX,
            @Param("endY") Double endY
    );
}