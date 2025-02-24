package com.dubu.backend.todo.application.impl.share;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.dto.MemberLocationDto;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infra.repository.LocationRedisRepository;
import com.dubu.backend.member.infra.repository.MemberRepository;
import com.dubu.backend.todo.dto.request.SurroundingMemberQueryRequest;
import com.dubu.backend.todo.application.ShareService;
import com.dubu.backend.todo.application.collection.share.MemberCategoryCollection;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.dto.response.*;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        List<MemberLocationInfo> memberLocationInfos = locationRedisRepository.findMemberLocations(memberId, request);

        if(memberLocationInfos == null || memberLocationInfos.isEmpty()){
            return null;
        }

        List<MemberCategoryInfo> memberCategoryInfos = todoRepository.findTodoCountGroupByCategory(extractMemberIds(memberLocationInfos));

        MemberCategoryCollection memberCategoryCollection = new MemberCategoryCollection(memberCategoryInfos);

        locationRedisRepository.saveMemberLocation(memberId, new MemberLocationDto(request.x_coordinate(), request.y_coordinate()));

        return ShareInfo.of(MemberInfo.from(memberLocationInfos, memberCategoryCollection.getMemberToCategories()), CategoryRankInfo.from(memberCategoryCollection.getCategoryMemberCount()));
    }

    private List<Long> extractMemberIds(List<MemberLocationInfo> memberLocationInfos) {
        return memberLocationInfos.stream()
                .map(MemberLocationInfo::memberId)
                .toList();
    }
}