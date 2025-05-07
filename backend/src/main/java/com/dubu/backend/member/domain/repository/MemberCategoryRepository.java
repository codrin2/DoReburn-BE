package com.dubu.backend.member.domain.repository;

import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.domain.model.MemberCategory;
import com.dubu.backend.todo.domain.past.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberCategoryRepository extends JpaRepository<MemberCategory, Long> {
    List<MemberCategory> findByMemberId(Long memberId);

    @Query("SELECT mc.category FROM MemberCategory mc WHERE mc.member.id = :memberId")
    List<Category> findCategoriesByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT mc.category.id FROM MemberCategory mc WHERE mc.member = :member")
    List<Long> findCategoryIdsByMember(@Param("member") Member member);

    @Query("SELECT mc FROM MemberCategory mc JOIN FETCH mc.category WHERE mc.member = :member")
    List<MemberCategory> findMemberCategoriesWithCategoryByMember(Member member);
}
