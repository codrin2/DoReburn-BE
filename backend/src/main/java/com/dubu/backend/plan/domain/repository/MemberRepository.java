package com.dubu.backend.plan.domain.repository;

import com.dubu.backend.plan.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
