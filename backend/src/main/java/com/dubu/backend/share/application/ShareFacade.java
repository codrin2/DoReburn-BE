package com.dubu.backend.share.application;

import com.dubu.backend.share.application.api.MemberApi;
import com.dubu.backend.share.application.api.TodoApi;
import com.dubu.backend.share.application.dto.SurroundingMemberFetchCommand;
import com.dubu.backend.share.application.dto.SurroundingMemberResult;
import com.dubu.backend.share.application.dto.SurroundingMembersWithRankResult;
import com.dubu.backend.share.domain.CategoryRank;
import com.dubu.backend.share.domain.SurroundingMember;
import com.dubu.backend.share.domain.RecentTodo;
import com.dubu.backend.share.domain.repository.MemberLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShareFacade {
    private final MemberLocationRepository memberLocationRepository;

    private final TodoApi todoApi;
    private final MemberApi memberApi;

    public SurroundingMembersWithRankResult findSurroundingMembersInfo(Long memberId, SurroundingMemberFetchCommand command){
        List<SurroundingMember> memberLocations = findSurroundingMemberLocation(memberId, command);
        List<Long> memberIds = extractMemberIds(memberLocations);

        List<RecentTodo> recentTodos = todoApi.getRecentTodosOfMembers(memberIds);

        assignRecentTodoCategoryToSurroundingMember(memberLocations, recentTodos);
        List<CategoryRank> categoryRanks = findCategoryRank(recentTodos);

        return SurroundingMembersWithRankResult.from(memberLocations, categoryRanks);
    }

    public SurroundingMemberResult findSurroundingMemberInfo(Long memberId, Long surroundingMemberId){
        SurroundingMember surroundingMember = memberApi.getSurroundingMember(surroundingMemberId);

        List<RecentTodo> recentTodos = todoApi.getRecentTodos(surroundingMemberId);

        return SurroundingMemberResult.from(surroundingMember, recentTodos);
    }

    public void deleteFromFavorites(Long memberId, Long surroundingMemberTodoId){
        todoApi.deleteFromFavorites(memberId, surroundingMemberTodoId);
    }

    private List<SurroundingMember> findSurroundingMemberLocation(Long memberId, SurroundingMemberFetchCommand command){
        return memberLocationRepository.findMemberLocations(memberId, command.x_coordinate(), command.y_coordinate(), command.radius());
    }

    public void assignRecentTodoCategoryToSurroundingMember(List<SurroundingMember> surroundingMembers, List<RecentTodo> recentTodos){
        Map<Long, Set<String>> memberIdCategoryMap = recentTodos.stream()
                .collect(Collectors.groupingBy(RecentTodo::getMemberId,
                        Collectors.mapping(RecentTodo::getCategory, Collectors.toSet())));

        surroundingMembers.forEach(
                sm -> sm.populateRecentTodoCategories(
                        memberIdCategoryMap.get(sm.getMemberId()).stream().toList()
                )
        );
    }

    private List<CategoryRank> findCategoryRank(List<RecentTodo> recentTodos){
        Map<String, Integer> categoryCountMap = new HashMap<>();
        recentTodos.forEach(rt -> categoryCountMap.merge(rt.getCategory(), 1, Integer::sum));

        return categoryCountMap.entrySet().stream()
                .map(entry -> CategoryRank.of(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(CategoryRank::getCount).reversed())
                .toList();
    }

    private List<Long> extractMemberIds(List<SurroundingMember> memberLocations){
        return memberLocations.stream()
                .map(SurroundingMember::getMemberId)
                .toList();
    }
}
