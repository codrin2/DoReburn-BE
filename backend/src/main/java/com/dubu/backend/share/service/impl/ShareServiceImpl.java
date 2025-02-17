package com.dubu.backend.share.service.impl;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.dto.MemberLocation;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infra.repository.LocationRedisRepository;
import com.dubu.backend.member.infra.repository.MemberRepository;
import com.dubu.backend.share.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.share.dto.response.CategoryInfo;
import com.dubu.backend.share.dto.response.MemberLocationInfo;
import com.dubu.backend.share.dto.response.ShareInfo;
import com.dubu.backend.share.service.ShareService;
import com.dubu.backend.todo.entity.Category;
import com.dubu.backend.todo.repository.CategoryRepository;
import com.dubu.backend.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

        Map<String, Long> todoCountGroupByCategoryForStopMembers = todoRepository.findTodoCountGroupByCategoryForStopMembers(splitStopMember(neighborhoodMembers), LocalDate.now());
        Map<String, Long> todoCountGroupByCategoryForMoveOrFeedbackMembers = todoRepository.findTodoCountGroupByCategoryForMoveOrFeedbackMembers(splitInProgressOrFeedbackMember(neighborhoodMembers));

        locationRedisRepository.saveMemberLocation(memberId, new MemberLocation(request.x_coordinate(), request.y_coordinate()));

        return ShareInfo.of(memberLocationInfos, CategoryInfo.merge(categories, todoCountGroupByCategoryForStopMembers, todoCountGroupByCategoryForMoveOrFeedbackMembers));
    }

    private List<Long> extractMemberIds(List<MemberLocationInfo> memberLocationInfos) {
        return memberLocationInfos.stream()
                .map(MemberLocationInfo::memberId)
                .toList();
    }

    private List<Member> splitStopMember(List<Member> members){
        return members.stream().filter(m -> m.getStatus().equals(Status.STOP)).toList();
    }

    private List<Member> splitInProgressOrFeedbackMember(List<Member> members) {
        return members.stream()
                .filter(m -> m.getStatus().equals(Status.MOVE) || m.getStatus().equals(Status.FEEDBACK))
                .toList();
    }
}