package com.dubu.backend.member.application;

import com.dubu.backend.member.api.response.MemberInfoResponse;
import com.dubu.backend.member.api.response.MemberSavedAddressResponse;
import com.dubu.backend.member.api.response.MemberStatusResponse;
import com.dubu.backend.member.application.dto.MemberDetailResult;
import com.dubu.backend.member.core.exception.MemberSavedAddressNotFoundException;
import com.dubu.backend.member.domain.model.Address;
import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.domain.repository.AddressRepository;
import com.dubu.backend.member.domain.repository.MemberCategoryRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.todo.domain.past.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dubu.backend.member.application.MemberServiceHelper.findExistingMember;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryFacade {
    private final TokenFacade tokenFacade;
    private final MemberRepository memberRepository;
    private final AddressRepository addressRepository;
    private final MemberCategoryRepository memberCategoryRepository;

    public Member getMemberByToken(String token) {
        Long memberId = tokenFacade.validateToken(token);

        return findExistingMember(memberRepository, memberId);
    }

    public MemberInfoResponse findMemberInfo(Long memberId) {
        Member currentMember = findExistingMember(memberRepository, memberId);

        List<Category> categories = memberCategoryRepository.findCategoriesByMemberId(memberId);

        List<Address> addresses = addressRepository.findByMemberId(memberId);

        return MemberInfoResponse.of(currentMember, categories, addresses);
    }

    public MemberStatusResponse findMemberStatus(Long memberId) {
        Member currentMember = findExistingMember(memberRepository, memberId);

        return new MemberStatusResponse(currentMember.getStatus().name());
    }

    public String findMemberNickname(Long memberId){
        Member member = findExistingMember(memberRepository, memberId);
        return member.getNickname();
    }

    public MemberDetailResult findMember(Long memberId){
        Member member = findExistingMember(memberRepository, memberId);
        return MemberDetailResult.from(member);
    }

    public MemberSavedAddressResponse findMemberSavedAddress(Long memberId) {
        findExistingMember(memberRepository, memberId);

        List<Address> addresses = addressRepository.findByMemberId(memberId);
        if (addresses.isEmpty()) {
            throw new MemberSavedAddressNotFoundException(memberId);
        }

        return MemberSavedAddressResponse.from(addresses);
    }

    public List<String> findMemberCategory(Long memberId){
        Member currentMember = findExistingMember(memberRepository, memberId);

        return memberCategoryRepository.findMemberCategoriesWithCategoryByMember(currentMember)
                .stream().map(memberCategory -> memberCategory.getCategory().getName())
                .toList();
    }
}