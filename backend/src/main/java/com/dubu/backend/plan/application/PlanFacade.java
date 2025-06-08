package com.dubu.backend.plan.application;

import com.dubu.backend.core.domain.event.FeedbackEndedEvent;
import com.dubu.backend.core.domain.event.PlanEndedEvent;
import com.dubu.backend.core.domain.event.PlanRemovedEvent;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.plan.core.exception.InvalidMemberStatusException;
import com.dubu.backend.plan.domain.enums.MemberStatus;
import com.dubu.backend.plan.application.api.MemberApi;
import com.dubu.backend.plan.application.api.TodoApi;
import com.dubu.backend.core.domain.event.PlanCreatedEvent;
import com.dubu.backend.plan.application.event.PlanEventPublisher;
import com.dubu.backend.plan.domain.*;
import com.dubu.backend.member.domain.model.TempMember;
import com.dubu.backend.member.domain.repository.TempMemberRepository;
import com.dubu.backend.notification.application.WebPushService;
import com.dubu.backend.plan.api.response.RecentPlanTodosResponse;
import com.dubu.backend.plan.domain.enums.TrafficType;
import com.dubu.backend.plan.domain.repository.dto.PlanSearchCond;
import com.dubu.backend.plan.domain.vo.PathIdentifier;
import com.dubu.backend.plan.api.request.PlanCreateRequest;
import com.dubu.backend.plan.api.request.PlanFeedbackCreateRequest;
import com.dubu.backend.plan.api.response.FeedbackWritePageInfoResponse;
import com.dubu.backend.plan.api.response.PlanRecentResponse;
import com.dubu.backend.plan.core.exception.PlanNotFoundException;
import com.dubu.backend.plan.core.exception.UnauthorizedPlanDeletionException;
import com.dubu.backend.plan.domain.repository.FeedbackRepository;
import com.dubu.backend.plan.domain.repository.SubPathRepository;
import com.dubu.backend.plan.domain.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanFacade {
    private final PathFacade pathFacade;
    private final WebPushService webPushService;

    private final TodoApi todoApi;
    private final MemberApi memberApi;

    private final TempMemberRepository tempMemberRepository;
    private final PlanRepository planRepository;
    private final SubPathRepository subPathRepository;
    private final FeedbackRepository feedbackRepository;

    private final PlanEventPublisher eventPublisher;

    @Transactional
    public Long savePlan(
            Long memberId,
            Double startX, Double startY,
            Double endX, Double endY,
            PlanCreateRequest request
    ) {
        Member currentMember = memberApi.getMember(memberId);

        // STOP인 상태의 유저만 사용 가능
        if (currentMember.getStatus() != MemberStatus.STOP) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        // 새로 만드는 경로가 기존에 저장된 경로인지 체크하기 위한 PathIdentifier 추출
        List<PathIdentifier> newPathIdentifiers = request.paths().stream()
                .map(path -> new PathIdentifier(path.trafficType(), path.startName(), path.endName()))
                .toList();

        // Route 재사용 여부 확인
        Path reusablePath = pathFacade.findReusableRoute(startX, startY, endX, endY, newPathIdentifiers);

        Plan newPlan = Plan.createPlan(currentMember, request.totalSectionTime());
        Plan savedPlan = planRepository.save(newPlan);

        Path finalPath = null;
        if (reusablePath == null) {
            finalPath = pathFacade.createNewRoute(startX, startY, endX, endY, request.totalTime());
        }

        // Path 생성 → route는 기존꺼면 null, 새 route 있으면 연결
        List<SubPath> subPaths = createAndSavePaths(savedPlan, finalPath, request);

        // 오늘의 할 일(Todo) Path 할당 & Todo 내용 복제하여 저장
        SubPath assignedSubPath = subPaths.stream()
                .filter(path -> path.getTrafficType()!= TrafficType.WALK)
                .findFirst()
                .orElse(null);

        eventPublisher.publishPlanCreatedEvent(new PlanCreatedEvent(currentMember.getId(), assignedSubPath != null ? assignedSubPath.getId(): null));
//        webPushService.sendPushAndMemberStatusChange(memberId, savedPlan);
        return savedPlan.getId();
    }

    @Transactional
    public Long savePlanFeedback(Long memberId, Long planId, PlanFeedbackCreateRequest request) {
        Member currentMember = memberApi.getMember(memberId);

        if (currentMember.getStatus() != MemberStatus.FEEDBACK) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        Plan currentPlan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException(planId));

        if (!currentPlan.getMemberId().equals(memberId)) {
            throw new UnauthorizedPlanDeletionException(memberId, planId);
        }

        Feedback newFeedback = Feedback.createFeedback(currentPlan, request.mood(), request.memo());
        feedbackRepository.save(newFeedback);
        eventPublisher.publishFeedbackEndedEvent(new FeedbackEndedEvent(memberId));

        return newFeedback.getId();
    }

    @Transactional(readOnly = true)
    public PlanRecentResponse findRecentPlan(Long memberId) {
        memberApi.getMember(memberId);

        Plan recentPlan = planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(PlanNotFoundException::new);

        List<SubPath> subPaths = subPathRepository.findByPlanIdOrderByPathOrderAsc(recentPlan.getId());

        List<Todo> todos = todoApi.getTodos(subPaths.stream().map(SubPath::getId).toList());

        assignTodosToSubPath(subPaths, todos);

        return PlanRecentResponse.of(recentPlan, subPaths);
    }

    // 임시
    @Transactional(readOnly = true)
    public RecentPlanTodosResponse findTodosOfRecentPlan(Long memberId){
        TempMember member = tempMemberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        Plan recentPlan = planRepository.findTopByMemberIdAndIsCompletedOrderByCreatedAtDesc(member.getId(), true)
            .orElseThrow(PlanNotFoundException::new);

        return RecentPlanTodosResponse.from(recentPlan.getTodos());
    }

    @Transactional(readOnly = true)
    protected List<Plan> findPlans(Member member, LocalDateTime startDate, LocalDateTime endDate){
        List<Plan> plans = planRepository.findPlans(PlanSearchCond.of(member.getId(), true, startDate, endDate));
        List<Long> subPathIds = plans.stream().map(Plan::getSubPaths).flatMap(List::stream).map(SubPath::getId).toList();
        List<Todo> todos = todoApi.getCompletedTodos(subPathIds);

        Map<Long, List<Todo>> subPahtIdTodosMap = todos.stream()
                .collect(Collectors.groupingBy(Todo::getSubPathId));

        for(Plan plan: plans){
            List<Todo> planTodos = plan.getSubPaths().stream()
                    .flatMap(sp -> subPahtIdTodosMap.getOrDefault(sp.getId(), Collections.emptyList()).stream())
                    .toList();
            plan.assignTodos(planTodos);
        }
        return plans;
    }

    @Transactional(readOnly = true)
    public List<Long> findSubPathIdOfRecentPlans(List<Long> memberIds){
        List<Long> planIds = planRepository.findCompletedRecentPlansId(memberIds);
        return subPathRepository.findByPlanIdIn(planIds);
    }

    @Transactional(readOnly = true)
    public FeedbackWritePageInfoResponse findFeedbackWritePageInfo(Long memberId) {
        Member currentMember = memberApi.getMember(memberId);

        if (currentMember.getStatus() != MemberStatus.FEEDBACK) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        Plan recentPlan = findRecentPlanWithTodos(memberId);

        return FeedbackWritePageInfoResponse.of(recentPlan);
    }

    @Transactional
    public void completeMove(Long memberId) {
        Member currentMember = memberApi.getMember(memberId);

        if (currentMember.getStatus() != MemberStatus.MOVE) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        Plan recentPlan = findRecentPlanWithTodos(memberId);

        List<Todo> todos = new ArrayList<>();
        List<Todo> doneTodos = new ArrayList<>();

        recentPlan.getSubPaths().forEach(path -> {
            List<Todo> pathTodos = path.getTodos();
            List<Todo> pathDoneTodos = pathTodos.stream().filter(todo -> todo.getIsCompleted() == Boolean.TRUE).toList();

            if(!pathDoneTodos.isEmpty()){
                int sectionTimePerTodo = path.getSectionTime() / pathDoneTodos.size();

                pathDoneTodos.forEach(doneTodo -> doneTodo.updateSpentTime(sectionTimePerTodo));
            }
            todos.addAll(pathTodos);
            doneTodos.addAll(pathDoneTodos);
        });

        eventPublisher.publishPlanEndedEvent(
                new PlanEndedEvent(memberId,
                        todos.stream().map(Todo::getId).toList(),
                        doneTodos.stream()
                                .map(t -> new PlanEndedEvent.DoneTodo(t.getId(), t.getSpentTime()))
                                .toList()
                ));

        recentPlan.updateIsCompleted(true);
    }

    @Transactional
    public void removePlan(Long memberId, Long planId) {
        Member currentMember = memberApi.getMember(memberId);

        if (currentMember.getStatus() != MemberStatus.MOVE) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        Plan planToDelete = planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException(planId));

        if (!planToDelete.getMemberId().equals(memberId)) {
            throw new UnauthorizedPlanDeletionException(memberId, planId);
        }
        eventPublisher.publishPlanRemovedEvent(new PlanRemovedEvent(memberId));
        planRepository.delete(planToDelete);
    }

    private List<SubPath> createAndSavePaths(Plan plan, Path path, PlanCreateRequest request) {
        AtomicInteger index = new AtomicInteger(0);
        List<SubPath> subPaths = request.paths().stream()
                .filter(pathRequest -> path != null || !Objects.equals(pathRequest.trafficType(), "WALK")) // route가 null이면 WALK 제외
                .map(pathRequest -> SubPath.createPath(
                        plan,
                        path,
                        pathRequest,
                        index.getAndIncrement()
                ))
                .toList();

        subPathRepository.saveAll(subPaths);
        return subPaths;
    }

    private Plan findRecentPlanWithTodos(Long memberId){
        Plan recentPlan = planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(PlanNotFoundException::new);

        List<Long> subPathIds = recentPlan.getSubPaths().stream()
                .map(SubPath::getId)
                .toList();

        List<Todo> todos = todoApi.getTodos(subPathIds);

        recentPlan.assignTodos(todos);
        assignTodosToSubPath(recentPlan.getSubPaths(), todos);

        return recentPlan;
    }

    private void assignTodosToSubPath(List<SubPath> subPaths, List<Todo> todos) {
        Map<Long, List<Todo>> subPathIdTodoMap = todos.stream()
                .collect(Collectors.groupingBy(Todo::getSubPathId));

        subPaths.forEach(path -> path.assignTodos(subPathIdTodoMap.get(path.getId())));
    }
}