package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.plan.domain.SubPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubPathRepository extends JpaRepository<SubPath, Long>{
    List<SubPath> findByPlanIdOrderByPathOrderAsc(Long id);

    @Query("SELECT sp.id FROM SubPath sp WHERE sp.plan.id in :planIds")
    List<Long> findByPlanIdIn(List<Long> planIds);
}