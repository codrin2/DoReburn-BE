package com.dubu.backend.share.service.impl;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.dto.MemberLocation;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infra.repository.LocationRedisRepository;
import com.dubu.backend.member.infra.repository.MemberRepository;
import com.dubu.backend.share.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.share.dto.response.*;
import com.dubu.backend.share.service.ShareService;
import com.dubu.backend.share.service.collection.MemberCategoryCollection;
import com.dubu.backend.todo.entity.Category;
import com.dubu.backend.todo.repository.CategoryRepository;
import com.dubu.backend.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
    private final LocationRedisRepository locationRedisRepository;

    @Override
    @Transactional
    public ShareInfo findSurroundingMembersInfo(Long memberId, SurroundingMemberQueryRequest request){
        memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        List<Category> categories = categoryRepository.findAll();

        List<MemberLocationInfo> memberLocationInfos = locationRedisRepository.findMemberLocations(request);

        if(memberLocationInfos == null || memberLocationInfos.isEmpty()){
            return null;
        }

        List<Member> neighborhoodMembers = memberRepository.findMembersByMemberIds(extractMemberIds(memberLocationInfos));

        List<MemberCategoryInfo> memberCategoryInfosForStopMembers = todoRepository.findTodoCountGroupByCategoryForStopMembers(splitStopMember(neighborhoodMembers), LocalDate.now());
        List<MemberCategoryInfo> memberCategoryInfosForMoveOrFeedbackMembers = todoRepository.findTodoCountGroupByCategoryForMoveOrFeedbackMembers(splitInMoveOrFeedbackMember(neighborhoodMembers));

        MemberCategoryCollection memberCategoryCollection = new MemberCategoryCollection(Stream.concat(
                memberCategoryInfosForStopMembers.stream(),
                memberCategoryInfosForMoveOrFeedbackMembers.stream()
        ).collect(Collectors.toList()));

        locationRedisRepository.saveMemberLocation(memberId, new MemberLocation(request.x_coordinate(), request.y_coordinate()));

        return ShareInfo.of(MemberInfo.from(memberLocationInfos, memberCategoryCollection.getMemberToCategories()), CategoryRankInfo.from(memberCategoryCollection.getCategoryMemberCount()));
    }

    private List<Long> extractMemberIds(List<MemberLocationInfo> memberLocationInfos) {
        return memberLocationInfos.stream()
                .map(MemberLocationInfo::memberId)
                .toList();
    }

    private List<Member> splitStopMember(List<Member> members){
        return members.stream().filter(m -> m.getStatus().equals(Status.STOP)).toList();
    }

    private List<Member> splitInMoveOrFeedbackMember(List<Member> members) {
        return members.stream()
                .filter(m -> m.getStatus().equals(Status.MOVE) || m.getStatus().equals(Status.FEEDBACK))
                .toList();
    }
}