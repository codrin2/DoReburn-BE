package com.dubu.backend.member.application;

import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.member.exception.RedisUnavailableException;
import com.dubu.backend.member.infrastructure.redis.RedisMemberLocationRepository;
import com.dubu.backend.member.domain.MemberLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;

import static com.dubu.backend.member.application.MemberServiceHelper.findExistingMember;

@Service
@RequiredArgsConstructor
public class MemberLocationFacade {
    private final MemberRepository memberRepository;
    private final RedisMemberLocationRepository redisMemberLocationRepository;

    public void updateMemberLocation(Long memberId, MemberLocation location) {
        findExistingMember(memberRepository, memberId);

        try {
            redisMemberLocationRepository.saveMemberLocation(memberId, location);
        }
        catch (RedisConnectionFailureException e) {
            throw new RedisUnavailableException();
        }
    }
}