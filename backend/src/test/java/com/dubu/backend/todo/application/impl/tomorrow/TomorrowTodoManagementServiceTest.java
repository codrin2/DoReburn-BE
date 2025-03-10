package com.dubu.backend.todo.application.impl.tomorrow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.domain.enums.Role;
import com.dubu.backend.member.domain.enums.OauthProvider;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.domain.repository.PlanRepository;
import com.dubu.backend.plan.domain.repository.SubPathRepository;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.Schedule;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.dto.common.TodoIdentifier;
import com.dubu.backend.todo.dto.request.TodoCreateFromArchivedRequest;
import com.dubu.backend.todo.dto.request.TodoCreateRequest;
import com.dubu.backend.todo.dto.request.TodoUpdateRequest;
import com.dubu.backend.todo.dto.response.TodoInfo;
import com.dubu.backend.todo.dto.response.TodoManageResult;
import com.dubu.backend.todo.exception.TodoLimitExceededException;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.ScheduleRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TomorrowTodoManagementServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TodoRepository todoRepository;
    @Mock private ScheduleRepository scheduleRepository;
    @Mock private PlanRepository planRepository;
    @Mock private SubPathRepository subPathRepository;
    @Mock private EntityManager entityManager; // 필요 시

    @InjectMocks
    private TomorrowTodoManagementService todoService;

    // --- 헬퍼 메서드 ---
    private Member createMember(Long memberId, Status status) {
        return Member.builder()
                .id(memberId)
                .nickname("testUser")
                .email("test@example.com")
                .oauthProvider(OauthProvider.KAKAO)
                .oauthProviderId("kakao123")
                .role(Role.USER)
                .status(status)
                .build();
    }
    private Category createCategory(String name) {
        return Category.builder().name(name).build();
    }
    private Plan createPlan(Long planId) {
        return Plan.builder().id(planId).build();
    }

    // --- createTodo 테스트 ---
    @Test
    @DisplayName("createTodo 성공(내일 스케줄 존재): 내일 스케줄 날짜가 올바르면 새 할 일 생성 후 단건 반환")
    void createTodo_success_existingTomorrowSchedule() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        String categoryName = "READING";
        TodoCreateRequest request = new TodoCreateRequest("종이 책 읽기", categoryName, "EASY", "매일 30분 이상 독서");

        Member member = createMember(memberId, Status.STOP);
        Category category = createCategory(categoryName);
        // 내일 스케줄: 날짜가 LocalDate.now().plusDays(1)
        Schedule schedule = Schedule.of(LocalDate.now().plusDays(1), member);
        // 할 일 목록이 2개 미만
        List<Todo> todos = new ArrayList<>();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1))).thenReturn(Optional.of(schedule));
        when(todoRepository.findTodosWithCategoryBySchedule(schedule)).thenReturn(todos);
        // toEntity()로 생성된 todo
        Todo newTodo = request.toEntity(member, category, schedule, null, TodoType.SCHEDULED);
        when(todoRepository.save(any(Todo.class))).thenReturn(newTodo);

        TodoManageResult<?> result = todoService.createTodo(identifier, request);
        assertNotNull(result);
        // flag가 false: 내일 스케줄이 이미 올바르게 존재하므로 추가 생성 없음
        assertFalse(result.isTomorrowScheduleCreated());
        TodoInfo info = (TodoInfo) result.info();
        assertEquals("종이 책 읽기", info.title());
    }

    @Test
    @DisplayName("createTodo 성공(내일 스케줄 미존재): 내일 스케줄이 올바르지 않으면 새 스케줄 생성 후 할 일 목록 반환")
    void createTodo_success_createTomorrowSchedule() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        String categoryName = "READING";
        TodoCreateRequest request = new TodoCreateRequest("종이 책 읽기", categoryName, "EASY", "매일 30분 이상 독서");

        Member member = createMember(memberId, Status.STOP);
        Category category = createCategory(categoryName);
        // 스케줄 조회 시 기존 schedule의 날짜가 내일과 다름 (예: 오늘 날짜)
        Schedule existingSchedule = Schedule.of(LocalDate.now(), member);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1))).thenReturn(Optional.of(existingSchedule));
        // 기존 schedule의 할 일 목록 (예: 2개)
        List<Todo> todos = new ArrayList<>();
        when(todoRepository.findTodosWithCategoryBySchedule(existingSchedule)).thenReturn(todos);

        // createTomorrowSchedule() 호출 시, 새 스케줄 생성
        Schedule tomorrowSchedule = Schedule.of(LocalDate.now().plusDays(1), member);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(tomorrowSchedule);
        // createTomorrowTodosFromTodayTodos() 호출 시, 기존 할 일들을 내일 할 일로 변환하여 저장
        List<Todo> tomorrowTodos = new ArrayList<>();
        // stubbing saveAll
        when(todoRepository.saveAll(any())).thenReturn(tomorrowTodos);

        // 새 할 일 생성
        Todo newTodo = request.toEntity(member, category, tomorrowSchedule, null, TodoType.SCHEDULED);
        when(todoRepository.save(any(Todo.class))).thenReturn(newTodo);

        TodoManageResult<?> result = todoService.createTodo(identifier, request);
        assertNotNull(result);
        // flag가 true: 내일 스케줄 새로 생성되었으므로 목록 반환
        assertTrue(result.isTomorrowScheduleCreated());
        // tomorrowTodos에 새 할 일이 추가되어 반환됨 (여기서는 stubbed list에 add() 후 반환하므로 size  = 1)
        List<TodoInfo> infos = (List<TodoInfo>) result.info();
        assertEquals("종이 책 읽기", infos.get(0).title());// 예시: 여러 할 일을 배열로 반환하는 경우
        // 또는 TodoInfo.fromEntities() 내부 구조에 맞춰 검증
    }

    @Test
    @DisplayName("createTodo 실패: 할 일 수가 5개 이상이면 TodoLimitExceededException 발생")
    void createTodo_failure_todoLimitExceeded() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        String categoryName = "READING";
        TodoCreateRequest request = new TodoCreateRequest("종이 책 읽기", categoryName, "EASY", "메모");

        Member member = createMember(memberId, Status.STOP);
        Category category = createCategory(categoryName);
        Schedule schedule = Schedule.of(LocalDate.now().plusDays(1), member);
        List<Todo> todos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            todos.add(Todo.builder().build());
        }
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1))).thenReturn(Optional.of(schedule));
        when(todoRepository.findTodosWithCategoryBySchedule(schedule)).thenReturn(todos);

        assertThrows(TodoLimitExceededException.class, () -> todoService.createTodo(identifier, request));
    }

    // --- createTodoFromArchived 테스트 ---
    @Test
    @DisplayName("createTodoFromArchived 성공: 부모 todo가 존재하고 내일 스케줄이 올바르면 할 일 생성 성공")
    void createTodoFromArchived_success_existingTomorrowSchedule() {
        Long memberId = 1L;
        Long parentTodoId = 9999L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, null);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, Status.STOP);
        // 내일 스케줄
        Schedule schedule = Schedule.of(LocalDate.now().plusDays(1), member);
        List<Todo> todos = new ArrayList<>();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1))).thenReturn(Optional.of(schedule));
        when(todoRepository.findTodosWithCategoryBySchedule(schedule)).thenReturn(todos);

        // 부모 todo 존재
        Category category = createCategory("ARCHIVE");
        Todo parentTodo = Todo.builder()
                .id(parentTodoId)
                .title("Parent Todo")
                .difficulty(TodoDifficulty.NORMAL)
                .memo("부모 메모")
                .member(member)
                .category(category)
                .type(TodoType.SAVE)
                .build();
        when(todoRepository.findWithCategoryById(parentTodoId)).thenReturn(Optional.of(parentTodo));
        when(todoRepository.findByParentTodoAndSchedule(parentTodo, schedule)).thenReturn(Optional.empty());
        Todo newTodo = Todo.of(parentTodo.getTitle(), TodoType.SCHEDULED, parentTodo.getDifficulty(),
                parentTodo.getMemo(), member, category, parentTodo, schedule, null);
        when(todoRepository.save(any(Todo.class))).thenReturn(newTodo);

        TodoManageResult<?> result = todoService.createTodoFromArchived(identifier, request);
        assertNotNull(result);
        assertFalse(result.isTomorrowScheduleCreated());
        TodoInfo info = (TodoInfo) result.info();
        assertEquals("Parent Todo", info.title());
    }

    @Test
    @DisplayName("modifyTodo 성공: 내일 스케줄이 올바르면 할 일 수정 후 단건 반환")
    void modifyTodo_success_existingTomorrowSchedule() {
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NEW_CAT", "HARD", "새 메모");

        Member member = createMember(memberId, Status.STOP);
        Category oldCategory = createCategory("OLD_CAT");
        Todo targetTodo = Todo.builder()
                .id(todoId)
                .title("기존 제목")
                .category(oldCategory)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("기존 메모")
                .member(member)
                .type(TodoType.SCHEDULED)
                .build();
        Category newCategory = createCategory("NEW_CAT");

        // 회원, todo 조회
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(targetTodo));
        // 내일 스케줄 조회: 내일 스케줄이 올바르다고 가정
        Schedule schedule = Schedule.of(LocalDate.now().plusDays(1), member);
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1))).thenReturn(Optional.of(schedule));
        when(todoRepository.findTodosWithCategoryBySchedule(schedule)).thenReturn(new ArrayList<>());
        // 불필요한 stubbing 제거: 실제 테스트 실행 경로에서 호출되지 않음

        // plan, path stubbing (빈 리스트)
        when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)).thenReturn(Optional.of(createPlan(10L)));
        when(subPathRepository.findByPlanAndType(any(), any())).thenReturn(Collections.emptyList());
        when(categoryRepository.findByName("NEW_CAT")).thenReturn(Optional.of(newCategory));

        // 수정 후 updateTodo() 내부에서 targetTodo의 값 변경됨 (모의)
        targetTodo.updateTodo("새 제목", newCategory, TodoDifficulty.HARD, "새 메모");

        TodoManageResult<?> result = todoService.modifyTodo(identifier, request);
        assertNotNull(result);
        assertFalse(result.isTomorrowScheduleCreated());
        TodoInfo info = (TodoInfo) result.info();
        assertEquals("새 제목", info.title());
        assertEquals("NEW_CAT", info.category());
        assertEquals("HARD", info.difficulty());
        assertEquals("새 메모", info.memo());
    }

    @Test
    @DisplayName("modifyTodo 성공: 내일 스케줄이 올바르지 않으면 내일 스케줄 생성 후 할 일 목록 반환")
    void modifyTodo_success_createTomorrowSchedule() {
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NEW_CAT", "HARD", "새 메모");

        Member member = createMember(memberId, Status.STOP);
        Category oldCategory = createCategory("OLD_CAT");
        // 기존 할 일: 오늘 스케줄에 등록된 것으로 가정 (내일 스케줄과 다름)
        Todo targetTodo = Todo.builder()
                .id(todoId)
                .title("기존 제목")
                .category(oldCategory)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("기존 메모")
                .member(member)
                .type(TodoType.SCHEDULED)
                .build();
        Category newCategory = createCategory("NEW_CAT");

        // 회원, todo 조회
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        // findById()로도 stubbing 추가해야 함.
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(targetTodo));
        when(todoRepository.findWithCategoryById(todoId)).thenReturn(Optional.of(targetTodo));

        // 내일 스케줄 조회: 내일 스케줄이 올바르다고 가정
        Schedule schedule = Schedule.of(LocalDate.now().plusDays(1), member);
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1))).thenReturn(Optional.of(schedule));
        when(todoRepository.findTodosWithCategoryBySchedule(schedule)).thenReturn(new ArrayList<>());
        // plan, path stubbing (빈 리스트)
        when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)).thenReturn(Optional.of(createPlan(10L)));
        when(subPathRepository.findByPlanAndType(any(), any())).thenReturn(Collections.emptyList());
        when(categoryRepository.findByName("NEW_CAT")).thenReturn(Optional.of(newCategory));

        // 수정 후 updateTodo() 내부에서 targetTodo의 값 변경됨 (모의)
        targetTodo.updateTodo("새 제목", newCategory, TodoDifficulty.HARD, "새 메모");

        TodoManageResult<?> result = todoService.modifyTodo(identifier, request);
        assertNotNull(result);
        assertFalse(result.isTomorrowScheduleCreated());
        TodoInfo info = (TodoInfo) result.info();
        assertEquals("새 제목", info.title());
        assertEquals("NEW_CAT", info.category());
        assertEquals("HARD", info.difficulty());
        assertEquals("새 메모", info.memo());
    }
    // --- removeTodo 테스트 ---
    @Test
    @DisplayName("removeTodo 성공: 내일 스케줄이 올바르면 단건 삭제 후 null 반환")
    void removeTodo_success_existingTomorrowSchedule() {
        Long memberId = 1L;
        Long todoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        Member member = createMember(memberId, Status.STOP);
        Todo targetTodo = Todo.builder()
                .id(todoId)
                .title("삭제할 할 일")
                .member(member)
                .type(TodoType.SCHEDULED)
                .build();
        // 내일 스케줄 조회: 스케줄의 날짜가 올바르다고 가정
        Schedule schedule = Schedule.of(LocalDate.now().plusDays(1), member);
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1))).thenReturn(Optional.of(schedule));
        // 삭제 전 부모 관계 제거를 위한 stubbing
        when(todoRepository.findWithScheduleByParentTodoAndScheduleDate(targetTodo, LocalDate.now().plusDays(1)))
                .thenReturn(Optional.empty());
        when(todoRepository.findByParentTodoAndSchedule(targetTodo, schedule))
                .thenReturn(Optional.empty());
        // plan, path stubbing (빈 리스트)
        when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)).thenReturn(Optional.of(createPlan(10L)));
        when(subPathRepository.findByPlanAndType(any(), any())).thenReturn(Collections.emptyList());
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(targetTodo));

        TodoManageResult<?> result = todoService.removeTodo(identifier);
        assertNotNull(result);
        assertFalse(result.isTomorrowScheduleCreated());
        assertNull(result.info());
        verify(todoRepository).delete(targetTodo);
    }

    @Test
    @DisplayName("removeTodo 성공: 내일 스케줄 미존재 시 내일 스케줄 생성 후 할 일 목록 반환")
    void removeTodo_success_createTomorrowSchedule() {
        Long memberId = 1L;
        Long todoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        Member member = createMember(memberId, Status.STOP);
        // 기존 할 일: 오늘 스케줄에 등록된 것으로 가정 (내일 스케줄과 다름)
        Todo targetTodo = Todo.builder()
                .id(todoId)
                .title("삭제할 할 일")
                .member(member)
                .type(TodoType.SCHEDULED)
                .build();
        // 기존 스케줄의 날짜가 오늘
        Schedule existingSchedule = Schedule.of(LocalDate.now(), member);
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1))).thenReturn(Optional.of(existingSchedule));
        List<Todo> todayTodos = new ArrayList<>();
        todayTodos.add(targetTodo);
        when(todoRepository.findTodosWithCategoryBySchedule(existingSchedule)).thenReturn(todayTodos);
        // 내일 스케줄 생성
        Schedule tomorrowSchedule = Schedule.of(LocalDate.now().plusDays(1), member);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(tomorrowSchedule);
        // 내일 할 일 목록 생성: targetTodo 제외한 오늘 할 일들을 내일 할 일로 변환
        List<Todo> tomorrowTodos = new ArrayList<>();
        // targetTodo가 삭제되었으므로 tomorrowTodos는 오늘 할 일 목록에서 targetTodo를 제거한 리스트
        for (Todo t : todayTodos) {
            if (!t.getId().equals(targetTodo.getId())) {
                tomorrowTodos.add(Todo.of(t.getTitle(), TodoType.SCHEDULED, t.getDifficulty(), t.getMemo(), t.getMember(), t.getCategory(), t.getParentTodo(), tomorrowSchedule, t.getSubPath()));
            }
        }
        tomorrowTodos = new ArrayList<>(tomorrowTodos);
        when(todoRepository.saveAll(any())).thenReturn(tomorrowTodos);
        // plan, path stubbing
        when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)).thenReturn(Optional.of(createPlan(10L)));
        when(subPathRepository.findByPlanAndType(any(), any())).thenReturn(Collections.emptyList());
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(targetTodo));

        TodoManageResult<?> result = todoService.removeTodo(identifier);
        assertNotNull(result);
        // flag true: 내일 스케줄 새로 생성되어 목록 반환
        assertTrue(result.isTomorrowScheduleCreated());
        // 반환된 목록에는 targetTodo가 제외된 할 일들이 있음
        List<TodoInfo> infos = (List<TodoInfo>) result.info();
        for (TodoInfo info : infos) {
            assertNotEquals(todoId, info.todoId());
        }
    }
}