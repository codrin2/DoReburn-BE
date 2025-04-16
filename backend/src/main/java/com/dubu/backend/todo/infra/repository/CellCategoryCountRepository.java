package com.dubu.backend.todo.infra.repository;

import com.dubu.backend.todo.domain.CellCategoryCount;
import com.dubu.backend.todo.domain.CellCategoryCountId;
import com.dubu.backend.todo.dto.response.CategoryCountInfo;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CellCategoryCountRepository extends JpaRepository<CellCategoryCount, CellCategoryCountId> {
    // @Query("SELECT new com.dubu.backend.todo.dto.response.CategoryCountInfo(c.name, SUM(ccc.count)) " +
    //         "FROM CellCategoryCount ccc JOIN ccc.category c " +
    //         "WHERE ccc.id.cellId IN :cellIds " +
    //         "GROUP BY ccc.id.categoryId")
    // List<CategoryCountInfo> findByCellIds(@Param("cellIds") List<String> cellIds);
}