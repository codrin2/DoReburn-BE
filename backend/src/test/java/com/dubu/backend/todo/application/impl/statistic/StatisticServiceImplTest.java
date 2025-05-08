package com.dubu.backend.todo.application.impl.statistic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dubu.backend.member.domain.enums.OauthProvider;
import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.domain.enums.Role;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.domain.Feedback;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.enums.Mood;
import com.dubu.backend.plan.domain.enums.TrafficType;
import com.dubu.backend.plan.domain.repository.SubPathRepository;
import com.dubu.backend.plan.domain.repository.PlanRepository;
import com.dubu.backend.todo.domain.past.Category;
import com.dubu.backend.todo.domain.past.Todo;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticServiceImplTest {

    @Mock private MemberRepository memberRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private PlanRepository planRepository;
    @Mock private TodoRepository todoRepository;
    @Mock private SubPathRepository subPathRepository;

    @InjectMocks
    private StatisticServiceImpl statisticService;

    private Member member;
    private final Long memberId = 1L;

    @BeforeEach
    void setUp() {
        // 헬퍼: 기본 MemberEntity
        member = Member.builder()
                .id(memberId)
                .nickname("TestUser")
                .email("test@example.com")
                .build();
    }

    // -----------------------------------------------
    // collectDayStatistic 테스트
    // -----------------------------------------------
    @Test
    @DisplayName("collectDayStatistic 성공: member 존재, dayPlans 존재 -> DayStatisticInfo 정상 반환 (partial mocking)")
    void collectDayStatistic_success_spyApproach() {
        // given
        LocalDate date = LocalDate.of(2023, 5, 3);

        // 1) 원본 MemberEntity 인스턴스 생성 (아직 createdAt은 null일 수도)
        //    - 실제 빌더로 만들되 createdAt 세팅 안 함 (어플리케이션 코드 수정 불가)
        Member realMember = Member.builder()
                .nickname("TestUser")
                .email("test@example.com")
                .oauthProvider(OauthProvider.KAKAO)
                .oauthProviderId("some-oauth-id")
                .role(Role.USER)
                .status(Status.ONBOARDING)
                .build();

        // 2) spy()를 사용해 부분 모킹
        Member spyMember = spy(realMember);

        // 3) spyMember.getCreatedAt() 호출 시 원하는 날짜 반환
        doReturn(LocalDateTime.of(2023, 5, 1, 0, 0))
                .when(spyMember).getCreatedAt();

        // 4) repository stubbing
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(spyMember));

        // 나머지 stubbing: 카테고리, plan, path 등
        List<Category> allCategories = List.of(
                Category.builder().id(100L).name("READING").build(),
                Category.builder().id(101L).name("EXERCISE").build()
        );
        when(categoryRepository.findAll()).thenReturn(allCategories);

        Plan plan1 = createPlan(1L, 50, createFeedback("MODERATE"));
        Plan plan2 = createPlan(2L, 30, createFeedback("MODERATE"));
        List<Plan> dayPlans = List.of(plan1, plan2);
        when(planRepository.findByMemberAndCreatedAtBetween(
                eq(spyMember),
                eq(date.atStartOfDay()),
                eq(date.atTime(LocalTime.MAX)))
        ).thenReturn(dayPlans);

        SubPath subPath1 = createPath(10L, 20);
        SubPath subPath2 = createPath(11L, 10);
        subPath1.getTodos().add(createTodo(100L, allCategories.get(0), TodoType.DONE));
        subPath1.getTodos().add(createTodo(101L, allCategories.get(1), TodoType.DONE));
        subPath2.getTodos().add(createTodo(102L, allCategories.get(0), TodoType.DONE));
        List<SubPath> daySubPaths = List.of(subPath1, subPath2);
        when(subPathRepository.findByPlansAndTypeAndIsCompleted(dayPlans, TodoType.DONE, true))
                .thenReturn(daySubPaths);

        // when
        DayStatisticInfo result = statisticService.collectDayStatistic(memberId, date);

        // then
        assertNotNull(result);
        // spyMember.getCreatedAt() → 2023-05-01 → .toLocalDate() == 2023-05-01
        assertEquals(LocalDate.of(2023, 5, 1), result.memberCreateDate());
        // totalUsageTime = 80
        assertEquals(80, result.totalUsageTime());
        // 할일 총 3
        assertEquals(3, result.totalTodoCount());
    }

    @Test
    @DisplayName("collectDayStatistic: dayPlans가 empty -> member.getCreatedAt()만 반환")
    void collectDayStatistic_noDayPlans() {
        // given
        LocalDate date = LocalDate.of(2023, 5, 3);
        Member member = Member.builder()
                .nickname("TestUser")
                .email("test@example.com")
                .oauthProvider(OauthProvider.KAKAO)
                .oauthProviderId("oauth_id_123")
                .role(Role.USER)
                .status(Status.ONBOARDING)
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "createdAt", LocalDateTime.of(2023, 5, 1, 0, 0));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findAll()).thenReturn(List.of());
        when(planRepository.findByMemberAndCreatedAtBetween(any(), any(), any())).thenReturn(List.of());

        // when
        DayStatisticInfo info = statisticService.collectDayStatistic(memberId, date);

        // then
        assertEquals(LocalDate.of(2023, 5, 1), info.memberCreateDate());
        Integer actualTodoCount = info.totalTodoCount();
        if (actualTodoCount == null) {
            actualTodoCount = 0;
        }
        assertEquals(0, actualTodoCount);
    }

    @Test
    @DisplayName("collectDayStatistic 실패: member 미존재 -> MemberNotFoundException")
    void collectDayStatistic_fail_memberNotFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class, () ->
                statisticService.collectDayStatistic(memberId, LocalDate.now()));
    }

    // -----------------------------------------------
    // collectWeekStatistic 테스트
    // -----------------------------------------------
    @Test
    @DisplayName("collectWeekStatistic 성공: spentTime도 Reflection으로 세팅")
    void collectWeekStatistic_success_spentTimeNotNull() {
        // given
        LocalDate startDate = LocalDate.of(2023, 5, 1);

        Member member = Member.builder()
                .nickname("WeekUser")
                .email("week@example.com")
                .oauthProvider(OauthProvider.KAKAO)
                .oauthProviderId("oauth_id_week")
                .role(Role.USER)
                .status(Status.ONBOARDING)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);
        ReflectionTestUtils.setField(member, "createdAt", LocalDateTime.of(2023, 4, 27, 0, 0));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        List<Category> categories = List.of(
                Category.builder().id(10L).name("READING").build(),
                Category.builder().id(11L).name("EXERCISE").build()
        );
        when(categoryRepository.findAll()).thenReturn(categories);

        Plan planA = createPlan(101L, 20, createFeedback("MODERATE"));
        ReflectionTestUtils.setField(planA, "createdAt", LocalDateTime.of(2023, 5, 2, 10, 0));
        Plan planB = createPlan(102L, 30, createFeedback("MODERATE"));
        ReflectionTestUtils.setField(planB, "createdAt", LocalDateTime.of(2023, 5, 3, 12, 0));
        // 각 plan에 대한 path 생성 및 할당 (sectionTime이 totalAvailableTime 산출에 사용됨)
        SubPath subPathA = createPathForPlan(planA, 111L, 20);  // sectionTime=20
        SubPath subPathB = createPathForPlan(planB, 112L, 30);  // sectionTime=30
        planA.getSubPaths().add(subPathA);
        planB.getSubPaths().add(subPathB);
        List<Plan> thisWeekPlans = List.of(planA, planB);
        when(planRepository.findWithPathsByMemberAndCreatedAtBetween(
                eq(member),
                eq(startDate.atStartOfDay()),
                eq(startDate.plusDays(6).atTime(LocalTime.MAX)))
        ).thenReturn(thisWeekPlans);

        List<Long> pathIds = List.of(111L, 112L);
        when(subPathRepository.findPathIdsByMemberAndCreatedAtBetween(
                eq(member),
                eq(startDate.atStartOfDay()),
                eq(startDate.plusDays(6).atTime(LocalTime.MAX)))
        ).thenReturn(pathIds);

        Todo todoA = createTodo(200L, categories.get(0), TodoType.DONE);
        ReflectionTestUtils.setField(todoA, "spentTime", 15);
        Todo todoB = createTodo(201L, categories.get(1), TodoType.DONE);
        ReflectionTestUtils.setField(todoB, "spentTime", 10);
        List<Todo> completedTodos = List.of(todoA, todoB);
        when(todoRepository.findByPathIdsAndTypeAndIsCompleted(pathIds, TodoType.DONE))
                .thenReturn(completedTodos);

        Plan lastWeekPlan = createPlan(999L, 25, createFeedback("MODERATE"));
        ReflectionTestUtils.setField(lastWeekPlan, "createdAt", LocalDateTime.of(2023, 4, 28, 8, 0));
        // lastWeekPlan에도 path를 할당하여 사용 시간 산출에 포함
        SubPath lastWeekSubPath = createPathForPlan(lastWeekPlan, 888L, 10);
        lastWeekPlan.getSubPaths().add(lastWeekSubPath);
        List<Plan> lastWeekPlans = List.of(lastWeekPlan);
        when(planRepository.findWithPathsByMemberAndCreatedAtBetween(
                eq(member),
                eq(startDate.minusWeeks(1).atStartOfDay()),
                eq(startDate.minusDays(1).atTime(LocalTime.MAX)))
        ).thenReturn(lastWeekPlans);

        // when
        WeekStatisticInfo result = statisticService.collectWeekStatistic(1L, startDate);

        // then
        assertNotNull(result);
        assertEquals(2, result.totalTodoCount());
        // totalAvailableTime는 planA(20) + planB(30) = 50
        assertEquals(50, result.totalAvailableTime());
    }

    @Test
    @DisplayName("collectWeekStatistic: thisWeekPlans empty -> creation date 반환")
    void collectWeekStatistic_noThisWeekPlans() {
        // given
        LocalDate startDate = LocalDate.of(2023, 5, 3);
        Member member = Member.builder()
                .nickname("TestMember")
                .email("test@example.com")
                .oauthProvider(OauthProvider.KAKAO)
                .oauthProviderId("oauth123")
                .role(Role.USER)
                .status(Status.ONBOARDING)
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "createdAt", LocalDateTime.of(2023, 5, 1, 0, 0));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findAll()).thenReturn(List.of());
        when(planRepository.findWithPathsByMemberAndCreatedAtBetween(
                eq(member),
                eq(startDate.atStartOfDay()),
                eq(startDate.plusDays(6).atTime(LocalTime.MAX))
        )).thenReturn(List.of());

        // when
        WeekStatisticInfo info = statisticService.collectWeekStatistic(memberId, startDate);

        // then
        assertNotNull(info);
        assertEquals(member.getCreatedAt().toLocalDate(), info.memberCreateDate());
        int totalTodoCount = info.totalTodoCount() == null ? 0 : info.totalTodoCount();
        assertEquals(0, totalTodoCount);
    }

    @Test
    @DisplayName("collectWeekStatistic 실패: member 미존재 -> MemberNotFoundException")
    void collectWeekStatistic_fail_memberNotFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class, () ->
                statisticService.collectWeekStatistic(memberId, LocalDate.now()));
    }

    // -----------------------------------------------
    // 헬퍼 메서드
    // -----------------------------------------------
    private Plan createPlan(Long planId, int totalTime, Feedback feedback) {
        return Plan.builder()
                .id(planId)
                .totalTime(totalTime)
                .feedback(feedback)
                .build();
    }

    private Feedback createFeedback(String mood) {
        return Feedback.builder()
                .mood(Mood.valueOf(mood))
                .build();
    }

    private SubPath createPath(Long pathId, int sectionTime) {
        SubPath subPath = SubPath.builder()
                .id(pathId)
                .sectionTime(sectionTime)
                .build();
        // path의 todos 리스트는 builder.default이거나 아래처럼
        subPath.getTodos().addAll(new ArrayList<>());
        return subPath;
    }

    private Todo createTodo(Long todoId, Category category, TodoType type) {
        return Todo.builder()
                .id(todoId)
                .category(category)
                .type(type)
                .isCompleted(true) // DONE일 경우 보통 완료 상태
                .build();
    }

    private SubPath createPathForPlan(Plan plan, Long pathId, Integer sectionTime) {
        Path path = Path.createRoute(0.0, 0.0, 0.0, 0.0, sectionTime);
        SubPath subPath = SubPath.builder()
                .plan(plan)
                .path(path)
                .trafficType(TrafficType.BUS) // 적절한 기본값 설정
                .startName("Start")
                .endName("End")
                .sectionTime(sectionTime)
                .pathOrder(1)
                .build();
        ReflectionTestUtils.setField(subPath, "id", pathId);
        return subPath;
    }
}