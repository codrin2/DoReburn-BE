package com.dubu.backend.todo.application.impl.share;

import com.dubu.backend.member.domain.MemberLocation;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.infrastructure.redis.RedisMemberLocationRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
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
    private final RedisMemberLocationRepository redisMemberLocationRepository;

    @Override
    @Transactional
    public ShareInfo findSurroundingMembersInfo(Long memberId, SurroundingMemberQueryRequest request){
        memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        List<Category> categories = categoryRepository.findAll();

        List<MemberLocationInfo> memberLocationInfos = redisMemberLocationRepository.findMemberLocations(memberId, request);

        if(memberLocationInfos == null || memberLocationInfos.isEmpty()){
            return null;
        }

        List<MemberCategoryInfo> memberCategoryInfos = todoRepository.findTodoCountGroupByCategory(extractMemberIds(memberLocationInfos));

        MemberCategoryCollection memberCategoryCollection = new MemberCategoryCollection(memberCategoryInfos);

        redisMemberLocationRepository.saveMemberLocation(memberId, new MemberLocation(request.x_coordinate(), request.y_coordinate()));

        return ShareInfo.of(MemberInfo.from(memberLocationInfos, memberCategoryCollection.getMemberToCategories()), CategoryRankInfo.from(memberCategoryCollection.getCategoryMemberCount()));
    }

    private List<Long> extractMemberIds(List<MemberLocationInfo> memberLocationInfos) {
        return memberLocationInfos.stream()
                .map(MemberLocationInfo::memberId)
                .toList();
    }
}