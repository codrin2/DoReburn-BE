package com.dubu.backend.plan.application;

import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.notification.application.WebPushService;
import com.dubu.backend.plan.domain.Feedback;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.enums.TrafficType;
import com.dubu.backend.plan.domain.vo.PathIdentifier;
import com.dubu.backend.plan.api.request.PlanCreateRequest;
import com.dubu.backend.plan.api.request.PlanFeedbackCreateRequest;
import com.dubu.backend.plan.api.response.FeedbackWritePageInfoResponse;
import com.dubu.backend.plan.api.response.PlanRecentResponse;
import com.dubu.backend.plan.core.exception.InvalidMemberStatusException;
import com.dubu.backend.plan.core.exception.PlanNotFoundException;
import com.dubu.backend.plan.core.exception.UnauthorizedPlanDeletionException;
import com.dubu.backend.plan.domain.repository.FeedbackRepository;
import com.dubu.backend.plan.domain.repository.SubPathRepository;
import com.dubu.backend.plan.domain.repository.PlanRepository;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.exception.ScheduleNotFoundException;
import com.dubu.backend.todo.infra.repository.ScheduleRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class PlanFacade {
    private final PathFacade pathFacade;
    private final WebPushService webPushService;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final SubPathRepository subPathRepository;
    private final ScheduleRepository scheduleRepository;
    private final TodoRepository todoRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public Long savePlan(
            Long memberId,
            Double startX, Double startY,
            Double endX, Double endY,
            PlanCreateRequest request
    ) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // STOP인 상태의 유저만 사용 가능
        if (currentMember.getStatus() != Status.STOP) {
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
        assignTodosToPaths(currentMember, subPaths);

        currentMember.updateStatus(Status.MOVE);

        webPushService.sendPushAndMemberStatusChange(memberId, savedPlan);

        return savedPlan.getId();
    }

    @Transactional
    public Long savePlanFeedback(Long memberId, Long planId, PlanFeedbackCreateRequest request) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (currentMember.getStatus() != Status.FEEDBACK) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        Plan currentPlan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException(planId));

        if (!currentPlan.getMember().getId().equals(memberId)) {
            throw new UnauthorizedPlanDeletionException(memberId, planId);
        }

        Feedback newFeedback = Feedback.createFeedback(currentPlan, request.mood(), request.memo());
        feedbackRepository.save(newFeedback);
        currentMember.updateStatus(Status.STOP);

        return newFeedback.getId();
    }

    @Transactional(readOnly = true)
    public PlanRecentResponse findRecentPlan(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        Plan recentPlan = planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(PlanNotFoundException::new);

        List<SubPath> subPaths = subPathRepository.findByPlanWithTodosOrderByPathOrder(recentPlan);

        return PlanRecentResponse.of(recentPlan, subPaths);
    }

    @Transactional(readOnly = true)
    public FeedbackWritePageInfoResponse findFeedbackWritePageInfo(Long memberId) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (currentMember.getStatus() != Status.FEEDBACK) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        Plan recentPlan = planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(PlanNotFoundException::new);

        return FeedbackWritePageInfoResponse.of(recentPlan);
    }

    @Transactional
    public void completeMove(Long memberId) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (currentMember.getStatus() != Status.MOVE) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        Plan recentPlan = planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(PlanNotFoundException::new);

        recentPlan.getSubPaths().forEach(path -> {
            List<Todo> todos = path.getTodos();
            List<Todo> doneTodos = todos.stream().filter(todo -> todo.getIsCompleted() == Boolean.TRUE).toList();

            if (!doneTodos.isEmpty()) {
                int sectionTimePerTodo = path.getSectionTime() / doneTodos.size();

                doneTodos.forEach(doneTodo -> doneTodo.updateSpentTime(sectionTimePerTodo));
            }

            todos.forEach(todo -> todo.updateTodoType(TodoType.DONE));
        });

        recentPlan.updateIsCompleted(true);
        currentMember.updateStatus(Status.FEEDBACK);
    }

    @Transactional
    public void removePlan(Long memberId, Long planId) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (currentMember.getStatus() != Status.MOVE) {
            throw new InvalidMemberStatusException(currentMember.getStatus().name());
        }

        Plan planToDelete = planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException(planId));

        if (!planToDelete.getMember().getId().equals(memberId)) {
            throw new UnauthorizedPlanDeletionException(memberId, planId);
        }
        currentMember.updateStatus(Status.STOP);

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

    /**
     * Schedule에서 Todo를 가져와 첫번째 Paths에 모두 할당
     */
    private void assignTodosToPaths(Member member, List<SubPath> subPaths) {
        Schedule schedule = scheduleRepository.findLatestSchedule(member, LocalDate.now())
                .orElseThrow(ScheduleNotFoundException::new);

        List<Todo> existingTodos = schedule.getTodos();
        List<Todo> newTodos = new ArrayList<>();
        SubPath assignedSubPath = subPaths.stream()
                .filter(path -> path.getTrafficType()!= TrafficType.WALK)
                .findFirst()
                .orElse(null);

        for (Todo original : existingTodos) {
            Todo cloned = Todo.copyWithPlan(member, original, assignedSubPath);
            newTodos.add(cloned);
        }
        todoRepository.saveAll(newTodos);
    }
}