package com.dubu.backend.member.infra.repository;

import com.dubu.backend.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByOauthProviderId(String providerId);

    @Query("SELECT m FROM Member m WHERE m.id IN :memberIds")
    List<Member> findMembersByMemberIds(List<Long> memberIds);
}