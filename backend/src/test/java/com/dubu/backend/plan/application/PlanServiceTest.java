package com.dubu.backend.plan.application;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.notification.application.NotificationService;
import com.dubu.backend.plan.domain.Feedback;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.enums.TrafficType;
import com.dubu.backend.plan.dto.request.PlanCreateRequest;
import com.dubu.backend.plan.dto.request.PlanFeedbackCreateRequest;
import com.dubu.backend.plan.dto.response.FeedbackWritePageInfoResponse;
import com.dubu.backend.plan.dto.response.PlanRecentResponse;
import com.dubu.backend.plan.exception.InvalidMemberStatusException;
import com.dubu.backend.plan.exception.PlanNotFoundException;
import com.dubu.backend.plan.exception.UnauthorizedPlanDeletionException;
import com.dubu.backend.plan.infra.repository.FeedbackRepository;
import com.dubu.backend.plan.infra.repository.PathRepository;
import com.dubu.backend.plan.infra.repository.PlanRepository;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.infra.repository.ScheduleRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@DisplayName("PlanService 단위 테스트")
class PlanServiceTest {

    @Mock
    private RouteService routeService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private PathRepository pathRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private PlanService planService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --------------------------------------------------------------------------------
    // savePlan
    // --------------------------------------------------------------------------------
    @Nested
    @DisplayName("[savePlan] 계획 생성")
    class Describe_savePlan {

        @Test
        @DisplayName("STOP 상태의 회원이면, 계획을 생성하고 상태를 MOVE로 변경한다.")
        void it_saves_plan_when_member_is_STOP() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(mockMember));
            given(mockMember.getStatus()).willReturn(Status.STOP);

            // 스케줄이 없으면 ScheduleNotFoundException 발생하므로 Mock 처리
            Schedule mockSchedule = mock(Schedule.class);
            given(scheduleRepository.findLatestSchedule(eq(mockMember), any()))
                    .willReturn(Optional.of(mockSchedule));
            given(mockSchedule.getTodos()).willReturn(List.of());

            PlanCreateRequest request = new PlanCreateRequest(
                    50, 40, List.of(
                    new PlanCreateRequest.Path("SUBWAY", 20, 1002, null, null, "선릉", "역삼"),
                    new PlanCreateRequest.Path("BUS", 20, null, "143", 1, "역삼", "강남")
            )
            );

            Plan savedPlan = mock(Plan.class);
            given(savedPlan.getId()).willReturn(12345L);
            given(planRepository.save(any(Plan.class))).willReturn(savedPlan);

            // when
            Long result = planService.savePlan(memberId, 127.0, 37.0, 126.9, 37.1, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(12345L);
            verify(memberRepository).findById(memberId);
            verify(mockMember).updateStatus(Status.MOVE);
            verify(planRepository).save(any(Plan.class));
            verify(notificationService).sendPushAndMemberStatusChange(eq(memberId), any(Plan.class));
        }

        @Test
        @DisplayName("회원 상태가 STOP이 아니면 InvalidMemberStatusException을 던진다.")
        void it_throws_InvalidMemberStatusException() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(mockMember));
            given(mockMember.getStatus()).willReturn(Status.MOVE);

            PlanCreateRequest request = new PlanCreateRequest(
                    50, 40, List.of()
            );

            // when & then
            assertThatThrownBy(() -> planService.savePlan(memberId, 127.0, 37.0, 126.9, 37.1, request))
                    .isInstanceOf(InvalidMemberStatusException.class);
        }

        @Test
        @DisplayName("존재하지 않는 회원이면 MemberNotFoundException을 던진다.")
        void it_throws_MemberNotFoundException() {
            // given
            Long memberId = 9999L;
            given(memberRepository.findById(memberId)).willReturn(Optional.empty());

            PlanCreateRequest request = new PlanCreateRequest(
                    50, 40, List.of()
            );

            // when & then
            assertThatThrownBy(() ->
                    planService.savePlan(memberId, 127.0, 37.0, 126.9, 37.1, request))
                    .isInstanceOf(MemberNotFoundException.class);
        }
    }

    // --------------------------------------------------------------------------------
    // savePlanFeedback
    // --------------------------------------------------------------------------------
    @Nested
    @DisplayName("[savePlanFeedback] 계획 피드백 생성")
    class Describe_savePlanFeedback {

        @Test
        @DisplayName("회원 상태가 FEEDBACK이면, 피드백을 저장하고 상태를 STOP으로 변경한다.")
        void it_saves_feedback_when_member_is_FEEDBACK() {
            // given
            Long memberId = 1L;
            Long planId = 10L;
            Member mockMember = mock(Member.class);
            Plan mockPlan = mock(Plan.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.FEEDBACK);
            when(planRepository.findById(planId)).thenReturn(Optional.of(mockPlan));
            when(mockPlan.getMember()).thenReturn(mockMember);
            when(mockMember.getId()).thenReturn(memberId);

            PlanFeedbackCreateRequest request = new PlanFeedbackCreateRequest("SATISFIED", "좋았어요!");

            Feedback savedFeedback = mock(Feedback.class);
            when(savedFeedback.getId()).thenReturn(999L);

            try (MockedStatic<Feedback> feedbackMock = mockStatic(Feedback.class)) {
                feedbackMock.when(() -> Feedback.createFeedback(eq(mockPlan), eq("SATISFIED"), eq("좋았어요!")))
                        .thenReturn(savedFeedback);
                when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedFeedback);

                // when
                Long feedbackId = planService.savePlanFeedback(memberId, planId, request);

                // then
                assertThat(feedbackId).isEqualTo(999L);
                verify(mockMember).updateStatus(Status.STOP);
            }
        }

        @Test
        @DisplayName("회원 상태가 FEEDBACK이 아니면 InvalidMemberStatusException 예외 발생")
        void it_throws_InvalidMemberStatusException() {
            // given
            Long memberId = 1L;
            Long planId = 10L;
            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.MOVE);

            PlanFeedbackCreateRequest request = new PlanFeedbackCreateRequest("SATISFIED", "좋았어요!");

            // when & then
            assertThatThrownBy(() -> planService.savePlanFeedback(memberId, planId, request))
                    .isInstanceOf(InvalidMemberStatusException.class);
        }

        @Test
        @DisplayName("존재하지 않는 계획이면 PlanNotFoundException 예외 발생")
        void it_throws_PlanNotFoundException() {
            // given
            Long memberId = 1L;
            Long planId = 9999L;
            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.FEEDBACK);

            when(planRepository.findById(planId)).thenReturn(Optional.empty());

            PlanFeedbackCreateRequest request = new PlanFeedbackCreateRequest("SATISFIED", "좋았어요!");

            // when & then
            assertThatThrownBy(() -> planService.savePlanFeedback(memberId, planId, request))
                    .isInstanceOf(PlanNotFoundException.class);
        }

        @Test
        @DisplayName("계획 회원 id와 현재 회원 id가 다르면 UnauthorizedPlanDeletionException 예외 발생")
        void it_throws_UnauthorizedPlanDeletionException() {
            // given
            Long memberId = 1L;
            Long planId = 10L;
            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.FEEDBACK);

            Plan mockPlan = mock(Plan.class);
            when(planRepository.findById(planId)).thenReturn(Optional.of(mockPlan));

            Member anotherMember = mock(Member.class);
            when(mockPlan.getMember()).thenReturn(anotherMember);
            when(anotherMember.getId()).thenReturn(2L);

            PlanFeedbackCreateRequest request = new PlanFeedbackCreateRequest("SATISFIED", "좋았어요!");

            // when & then
            assertThatThrownBy(() -> planService.savePlanFeedback(memberId, planId, request))
                    .isInstanceOf(UnauthorizedPlanDeletionException.class);
        }
    }

    // --------------------------------------------------------------------------------
    // findRecentPlan
    // --------------------------------------------------------------------------------
    @Nested
    @DisplayName("[findRecentPlan] 최근 계획 조회")
    class Describe_findRecentPlan {

        @Test
        @DisplayName("회원이 존재하고, 가장 최근 플랜이 존재하면 PlanRecentResponse 반환")
        void it_returns_PlanRecentResponse() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

            Plan mockPlan = mock(Plan.class);
            when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId))
                    .thenReturn(Optional.of(mockPlan));

            Path mockPath1 = mock(Path.class);
            Path mockPath2 = mock(Path.class);
            when(mockPath1.getTrafficType()).thenReturn(TrafficType.SUBWAY);
            when(mockPath2.getTrafficType()).thenReturn(TrafficType.BUS);

            when(pathRepository.findByPlanWithTodosOrderByPathOrder(mockPlan))
                    .thenReturn(List.of(mockPath1, mockPath2));

            // when
            PlanRecentResponse result = planService.findRecentPlan(memberId);

            // then
            assertThat(result).isNotNull();
            verify(memberRepository).findById(memberId);
            verify(planRepository).findTopByMemberIdOrderByCreatedAtDesc(memberId);
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 MemberNotFoundException 예외 발생")
        void it_throws_MemberNotFoundException() {
            // given
            Long memberId = 9999L;
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> planService.findRecentPlan(memberId))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("최근 플랜이 존재하지 않으면 PlanNotFoundException 예외 발생")
        void it_throws_PlanNotFoundException() {
            // given
            Long memberId = 1L;
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));
            when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> planService.findRecentPlan(memberId))
                    .isInstanceOf(PlanNotFoundException.class);
        }
    }

    // --------------------------------------------------------------------------------
    // findFeedbackWritePageInfo
    // --------------------------------------------------------------------------------
    @Nested
    @DisplayName("[findFeedbackWritePageInfo] 피드백 작성 페이지 정보 조회")
    class Describe_findFeedbackWritePageInfo {

        @Test
        @DisplayName("회원 상태가 FEEDBACK이면, FeedbackWritePageInfoResponse를 반환")
        void it_returns_FeedbackWritePageInfoResponse() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.FEEDBACK);

            Plan mockPlan = mock(Plan.class);
            when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId))
                    .thenReturn(Optional.of(mockPlan));

            // when
            FeedbackWritePageInfoResponse result = planService.findFeedbackWritePageInfo(memberId);

            // then
            assertThat(result).isNotNull();
            verify(memberRepository).findById(memberId);
            verify(planRepository).findTopByMemberIdOrderByCreatedAtDesc(memberId);
        }

        @Test
        @DisplayName("회원 상태가 FEEDBACK이 아니면 InvalidMemberStatusException 예외")
        void it_throws_InvalidMemberStatusException() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.MOVE);

            // when & then
            assertThatThrownBy(() -> planService.findFeedbackWritePageInfo(memberId))
                    .isInstanceOf(InvalidMemberStatusException.class);
        }

        @Test
        @DisplayName("최근 플랜이 존재하지 않으면 PlanNotFoundException 예외")
        void it_throws_PlanNotFoundException() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.FEEDBACK);

            when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> planService.findFeedbackWritePageInfo(memberId))
                    .isInstanceOf(PlanNotFoundException.class);
        }
    }

    // --------------------------------------------------------------------------------
    // completeMove
    // --------------------------------------------------------------------------------
    @Nested
    @DisplayName("[completeMove] 이동 완료 업데이트")
    class Describe_completeMove {

        @Test
        @DisplayName("회원 상태가 MOVE이면, 최근 플랜을 완료 처리하고 회원을 FEEDBACK 상태로 변경")
        void it_updates_plan_and_member_to_FEEDBACK() {
            // given
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.MOVE);

            Plan mockPlan = mock(Plan.class);
            when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId))
                    .thenReturn(Optional.of(mockPlan));

            Path mockPath = mock(Path.class);
            Todo todo1 = mock(Todo.class);
            Todo todo2 = mock(Todo.class);

            when(mockPath.getTodos()).thenReturn(List.of(todo1, todo2));
            when(mockPath.getTrafficType()).thenReturn(TrafficType.SUBWAY);
            when(mockPath.getSectionTime()).thenReturn(40);

            when(mockPlan.getPaths()).thenReturn(List.of(mockPath));

            when(todo1.getIsCompleted()).thenReturn(true);
            when(todo2.getIsCompleted()).thenReturn(true);

            // when
            planService.completeMove(memberId);

            // then
            verify(mockMember).updateStatus(Status.FEEDBACK);
            verify(mockPlan).updateIsCompleted(true);
            verify(todo1).updateSpentTime(20);
            verify(todo2).updateSpentTime(20);
            verify(todo1).updateTodoType(TodoType.DONE);
            verify(todo2).updateTodoType(TodoType.DONE);
        }

        @Test
        @DisplayName("회원 상태가 MOVE가 아니면 InvalidMemberStatusException 예외")
        void it_throws_InvalidMemberStatusException() {
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.STOP);

            assertThatThrownBy(() -> planService.completeMove(memberId))
                    .isInstanceOf(InvalidMemberStatusException.class);
        }

        @Test
        @DisplayName("최근 플랜이 없다면 PlanNotFoundException 예외")
        void it_throws_PlanNotFoundException() {
            Long memberId = 1L;
            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.MOVE);

            when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> planService.completeMove(memberId))
                    .isInstanceOf(PlanNotFoundException.class);
        }
    }

    // --------------------------------------------------------------------------------
    // removePlan
    // --------------------------------------------------------------------------------
    @Nested
    @DisplayName("[removePlan] 계획 삭제")
    class Describe_removePlan {

        @Test
        @DisplayName("회원 상태가 MOVE이면, 해당 planId를 삭제하고 상태를 STOP으로 변경")
        void it_removes_plan_when_member_in_MOVE() {
            // given
            Long memberId = 1L;
            Long planId = 100L;

            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.MOVE);

            Plan mockPlan = mock(Plan.class);
            when(planRepository.findById(planId)).thenReturn(Optional.of(mockPlan));

            when(mockPlan.getMember()).thenReturn(mockMember);
            when(mockMember.getId()).thenReturn(memberId);

            // when
            planService.removePlan(memberId, planId);

            // then
            verify(planRepository).delete(mockPlan);
            verify(mockMember).updateStatus(Status.STOP);
        }

        @Test
        @DisplayName("회원 상태가 MOVE가 아니면 InvalidMemberStatusException 예외")
        void it_throws_InvalidMemberStatusException() {
            Long memberId = 1L;
            Long planId = 100L;

            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.STOP);

            assertThatThrownBy(() -> planService.removePlan(memberId, planId))
                    .isInstanceOf(InvalidMemberStatusException.class);
        }

        @Test
        @DisplayName("계획이 존재하지 않으면 PlanNotFoundException 예외")
        void it_throws_PlanNotFoundException() {
            Long memberId = 1L;
            Long planId = 9999L;

            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.MOVE);

            when(planRepository.findById(planId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> planService.removePlan(memberId, planId))
                    .isInstanceOf(PlanNotFoundException.class);
        }

        @Test
        @DisplayName("해당 플랜의 회원 id와 다른 경우 UnauthorizedPlanDeletionException 예외")
        void it_throws_UnauthorizedPlanDeletionException() {
            Long memberId = 1L;
            Long planId = 10L;

            Member mockMember = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(mockMember.getStatus()).thenReturn(Status.MOVE);
            when(mockMember.getId()).thenReturn(memberId);

            Plan mockPlan = mock(Plan.class);
            Member anotherMember = mock(Member.class);
            when(anotherMember.getId()).thenReturn(2L);

            when(planRepository.findById(planId)).thenReturn(Optional.of(mockPlan));
            when(mockPlan.getMember()).thenReturn(anotherMember);

            assertThatThrownBy(() -> planService.removePlan(memberId, planId))
                    .isInstanceOf(UnauthorizedPlanDeletionException.class);
        }
    }
}