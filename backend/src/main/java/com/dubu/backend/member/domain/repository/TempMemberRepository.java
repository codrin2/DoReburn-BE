package com.dubu.backend.member.domain.repository;

import com.dubu.backend.member.domain.model.TempMember;
import io.lettuce.core.dynamic.annotation.Param;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TempMemberRepository extends JpaRepository<TempMember, Long> {
    @Query("SELECT tm.id FROM TempMember tm WHERE ST_Contains(ST_Buffer(:location, :radius), tm.location) ORDER BY tm.updatedAt desc LIMIT 100")
    List<TempMember> findByLocation(@Param("location") Point location, @Param("location") double radius);
}
