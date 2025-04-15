package com.dubu.backend.member.infrastructure;

import com.dubu.backend.member.domain.model.CellCategoryMemberCount;
import com.dubu.backend.member.domain.model.CellCategoryMemberCountId;
import com.dubu.backend.member.domain.repository.CellCategoryMemberCountRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MysqlCellCategoryMemberCountRepository extends JpaRepository<CellCategoryMemberCount, CellCategoryMemberCountId>, CellCategoryMemberCountRepository {
    @Override
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO cell_category_member_count (cell_id, category_id, count, created_at, updated_at) " +
            "VALUES (:cellId, :categoryId, 1, NOW(), NOW()) \n" +
            "ON DUPLICATE KEY UPDATE count = count + :delta, updated_at = NOW() \n",
            nativeQuery = true)
    void adjustCellCategoryMemberCount(String cellId, Long categoryId, int delta);
}
