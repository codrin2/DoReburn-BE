package com.dubu.backend.member.application;

import com.dubu.backend.member.domain.Address;
import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.repository.AddressRepository;
import com.dubu.backend.member.domain.repository.MemberCategoryRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.member.exception.MemberSavedAddressNotFoundException;
import com.dubu.backend.member.presentation.response.MemberInfoResponse;
import com.dubu.backend.member.presentation.response.MemberSavedAddressResponse;
import com.dubu.backend.member.presentation.response.MemberStatusResponse;
import com.dubu.backend.todo.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dubu.backend.member.application.MemberServiceHelper.findExistingMember;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryFacade {
    private final MemberRepository memberRepository;
    private final AddressRepository addressRepository;
    private final MemberCategoryRepository memberCategoryRepository;

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