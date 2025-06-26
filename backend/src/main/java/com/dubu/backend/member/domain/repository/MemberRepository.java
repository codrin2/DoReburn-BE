package com.dubu.backend.member.domain.repository;

import com.dubu.backend.member.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.id IN :memberIds")
    List<Member> findMembersByMemberIds(List<Long> memberIds);
}