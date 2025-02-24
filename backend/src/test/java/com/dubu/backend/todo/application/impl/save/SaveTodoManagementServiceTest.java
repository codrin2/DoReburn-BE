package com.dubu.backend.todo.application.impl.save;

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
import com.dubu.backend.auth.domain.OauthProvider;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.plan.domain.Path;
import com.dubu.backend.plan.domain.Plan;
import com.dubu.backend.plan.exception.InvalidMemberStatusException;
import com.dubu.backend.plan.exception.PlanNotFoundException;
import com.dubu.backend.plan.infra.repository.PathRepository;
import com.dubu.backend.plan.infra.repository.PlanRepository;
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
import com.dubu.backend.todo.exception.AlreadyAddedTodoFromArchivedException;
import com.dubu.backend.todo.exception.CategoryNotFoundException;
import com.dubu.backend.todo.exception.ScheduleNotFoundException;
import com.dubu.backend.todo.exception.TodoLimitExceededException;
import com.dubu.backend.todo.exception.TodoNotFoundException;
import com.dubu.backend.todo.exception.TodoTypeMismatchException;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.ScheduleRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import com.dubu.backend.member.infra.repository.MemberRepository;
import com.dubu.backend.todo.application.impl.save.SaveTodoManagementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaveTodoManagementServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TodoRepository todoRepository;
    @Mock private ScheduleRepository scheduleRepository;
    @Mock private PlanRepository planRepository;
    @Mock private PathRepository pathRepository;

    @InjectMocks
    private SaveTodoManagementService todoService;

    // 헬퍼: Member 생성 (SaveTodo의 경우, 회원 상태는 STOP 또는 MOVE여야 함)
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

    // 헬퍼: Category 생성
    private Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .build();
    }

    // 헬퍼: Plan 생성 (modifyTodo, removeTodo에서 사용)
    private Plan createPlan(Long planId) {
        return Plan.builder()
                .id(planId)
                .build();
    }

    // =========================================================
    // createTodo 메서드 테스트
    // =========================================================

    @Test
    @DisplayName("createTodo 성공: 유효한 회원(STOP), 카테고리 존재 시 todo 생성 성공")
    void givenValidInputs_whenCreateTodo_thenSuccess() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        String categoryName = "READING";
        TodoCreateRequest request = new TodoCreateRequest("종이 책 읽기", categoryName, "EASY", "매일 30분 이상 독서");

        Member member = createMember(memberId, Status.STOP);
        Category category = createCategory(categoryName);
        // SaveTodo의 경우 Schedule은 사용하지 않으므로 null 전달
        Todo todo = request.toEntity(member, category, null, null, TodoType.SAVE);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoManageResult<?> result = todoService.createTodo(identifier, request);
        assertNotNull(result);
        // 캐스팅: 실제 반환되는 info는 TodoInfo
        TodoInfo info = (TodoInfo) result.info();
        assertEquals("종이 책 읽기", info.title());
        assertEquals(categoryName, info.category());
    }

    @Test
    @DisplayName("createTodo 실패: 회원 미존재 시 MemberNotFoundException 발생")
    void givenNonExistingMember_whenCreateTodo_thenThrowMemberNotFoundException() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        TodoCreateRequest request = new TodoCreateRequest("종이 책 읽기", "READING", "EASY", "매일 30분 이상 독서");

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class, () -> todoService.createTodo(identifier, request));
    }

    @Test
    @DisplayName("createTodo 실패: 회원 상태가 ONBOARDING 또는 FEEDBACK이면 InvalidMemberStatusException 발생")
    void givenInvalidMemberStatus_whenCreateTodo_thenThrowInvalidMemberStatusException() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        TodoCreateRequest request = new TodoCreateRequest("종이 책 읽기", "READING", "EASY", "매일 30분 이상 독서");

        // ONBOARDING 상태는 허용되지 않음
        Member member = createMember(memberId, Status.ONBOARDING);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        assertThrows(InvalidMemberStatusException.class, () -> todoService.createTodo(identifier, request));
    }

    @Test
    @DisplayName("createTodo 실패: 카테고리 미존재 시 CategoryNotFoundException 발생")
    void givenNonExistingCategory_whenCreateTodo_thenThrowCategoryNotFoundException() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        String categoryName = "READING";
        TodoCreateRequest request = new TodoCreateRequest("종이 책 읽기", categoryName, "EASY", "매일 30분 이상 독서");

        Member member = createMember(memberId, Status.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());
        assertThrows(CategoryNotFoundException.class, () -> todoService.createTodo(identifier, request));
    }

    // =========================================================
    // createTodoFromArchived 메서드 테스트
    // =========================================================

    @Test
    @DisplayName("createTodoFromArchived 성공: 유효한 회원, 부모 todo 존재 시 todo 생성 성공")
    void givenValidInputs_whenCreateTodoFromArchived_thenSuccess() {
        Long memberId = 1L;
        Long parentTodoId = 9999L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, null);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, Status.STOP);
        // 부모 todo (저장된 할 일) 생성
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

        // 기존에 저장된 같은 아카이브 할 일이 없다고 가정
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(parentTodoId)).thenReturn(Optional.of(parentTodo));
        when(todoRepository.findByMemberAndParentTodoAndType(member, parentTodo, TodoType.SAVE))
                .thenReturn(Optional.empty());

        Todo newTodo = Todo.of(parentTodo.getTitle(), TodoType.SAVE, parentTodo.getDifficulty(),
                parentTodo.getMemo(), member, parentTodo.getCategory(), parentTodo, null, null);
        when(todoRepository.save(any(Todo.class))).thenReturn(newTodo);

        TodoManageResult<?> result = todoService.createTodoFromArchived(identifier, request);
        assertNotNull(result);
        TodoInfo info = (TodoInfo) result.info();
        assertEquals("Parent Todo", info.title());
    }

    @Test
    @DisplayName("createTodoFromArchived 실패: 부모 todo 미존재 시 TodoNotFoundException 발생")
    void givenNonExistingParentTodo_whenCreateTodoFromArchived_thenThrowTodoNotFoundException() {
        Long memberId = 1L;
        Long parentTodoId = 9999L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, null);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, Status.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(parentTodoId)).thenReturn(Optional.empty());
        assertThrows(TodoNotFoundException.class, () -> todoService.createTodoFromArchived(identifier, request));
    }

    @Test
    @DisplayName("createTodoFromArchived 실패: 이미 같은 아카이브 todo가 존재하면 AlreadyAddedTodoFromArchivedException 발생")
    void givenAlreadyAddedArchivedTodo_whenCreateTodoFromArchived_thenThrowAlreadyAddedTodoFromArchivedException() {
        Long memberId = 1L;
        Long parentTodoId = 9999L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, null);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, Status.STOP);
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

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(parentTodoId)).thenReturn(Optional.of(parentTodo));
        // 이미 같은 아카이브 할 일이 존재하는 경우
        when(todoRepository.findByMemberAndParentTodoAndType(member, parentTodo, TodoType.SAVE))
                .thenReturn(Optional.of(Todo.builder().build()));

        assertThrows(AlreadyAddedTodoFromArchivedException.class,
                () -> todoService.createTodoFromArchived(identifier, request));
    }

    // =========================================================
    // modifyTodo 메서드 테스트
    // =========================================================

    @Test
    @DisplayName("modifyTodo 성공: 유효한 회원, todo, 수정 요청 시 todo 수정 성공")
    void givenValidInputs_whenModifyTodo_thenSuccess() {
        // Given
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NEW_CAT", "HARD", "새 메모");

        Member member = createMember(memberId, Status.STOP);
        Category oldCategory = createCategory("OLD_CAT");
        Todo todo = Todo.builder()
                .id(todoId)
                .title("기존 제목")
                .category(oldCategory)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("기존 메모")
                .member(member)
                .type(TodoType.SAVE)
                .build();
        Category newCategory = createCategory("NEW_CAT");

        // 추가: 회원 stubbing
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // 나머지 stubbing...
        when(todoRepository.findWithScheduleByParentTodoAndScheduleDate(todo, LocalDate.now().plusDays(1)))
                .thenReturn(Optional.empty());
        Schedule todaySchedule = Schedule.of(LocalDate.now(), member);
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(todaySchedule));
        when(todoRepository.findByParentTodoAndSchedule(todo, todaySchedule))
                .thenReturn(Optional.empty());
        Plan latestPlan = createPlan(10L);
        when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId))
                .thenReturn(Optional.of(latestPlan));
        when(pathRepository.findByPlanAndType(latestPlan, TodoType.IN_PROGRESS))
                .thenReturn(Collections.emptyList());
        when(categoryRepository.findByName("NEW_CAT")).thenReturn(Optional.of(newCategory));
        when(todoRepository.findWithCategoryById(todoId)).thenReturn(Optional.of(todo));

        TodoManageResult<?> result = todoService.modifyTodo(identifier, request);
        assertNotNull(result);
        TodoInfo info = (TodoInfo) result.info();
        assertEquals("새 제목", info.title());
        assertEquals("NEW_CAT", info.category());
        assertEquals("HARD", info.difficulty());
        assertEquals("새 메모", info.memo());
    }

    @Test
    @DisplayName("modifyTodo 실패: 회원 미존재 시 MemberNotFoundException 발생")
    void givenNonExistingMember_whenModifyTodo_thenThrowMemberNotFoundException() {
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NEW_CAT", "HARD", "새 메모");

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class, () -> todoService.modifyTodo(identifier, request));
    }

    @Test
    @DisplayName("modifyTodo 실패: todo 미존재 시 TodoNotFoundException 발생")
    void givenNonExistingTodo_whenModifyTodo_thenThrowTodoNotFoundException() {
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NEW_CAT", "HARD", "새 메모");

        Member member = createMember(memberId, Status.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(todoId)).thenReturn(Optional.empty());
        assertThrows(TodoNotFoundException.class, () -> todoService.modifyTodo(identifier, request));
    }

    @Test
    @DisplayName("modifyTodo 실패: todo 타입 불일치 시 TodoTypeMismatchException 발생")
    void givenTodoTypeMismatch_whenModifyTodo_thenThrowTodoTypeMismatchException() {
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NEW_CAT", "HARD", "새 메모");

        Member member = createMember(memberId, Status.STOP);
        // 기존 todo의 타입이 SAVE가 아닌 경우
        Todo todo = Todo.builder()
                .id(todoId)
                .title("기존 제목")
                .member(member)
                .type(TodoType.SCHEDULED)
                .build();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(todoId)).thenReturn(Optional.of(todo));
        assertThrows(TodoTypeMismatchException.class, () -> todoService.modifyTodo(identifier, request));
    }

    @Test
    @DisplayName("modifyTodo 실패: 수정 요청 카테고리가 존재하지 않으면 CategoryNotFoundException 발생")
    void givenNonExistingCategory_whenModifyTodo_thenThrowCategoryNotFoundException() {
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        // 수정 요청 시 업데이트할 카테고리 이름이 존재하지 않음
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NON_EXIST_CAT", "HARD", "새 메모");

        Member member = createMember(memberId, Status.STOP);
        Category oldCategory = createCategory("OLD_CAT");
        Todo todo = Todo.builder()
                .id(todoId)
                .title("기존 제목")
                .category(oldCategory)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("기존 메모")
                .member(member)
                .type(TodoType.SAVE)
                .build();

        // 스텁 설정
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(todoId)).thenReturn(Optional.of(todo));

        // 수정 과정에서 스케줄, plan, path 관련 호출에 대해 정상적인 값을 반환하도록 stubbing
        Schedule todaySchedule = Schedule.of(LocalDate.now(), member);
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(todaySchedule));
        when(todoRepository.findWithScheduleByParentTodoAndScheduleDate(todo, LocalDate.now().plusDays(1)))
                .thenReturn(Optional.empty());
        when(todoRepository.findByParentTodoAndSchedule(todo, todaySchedule))
                .thenReturn(Optional.empty());

        // stubbing for plan and path
        Plan latestPlan = createPlan(10L);
        when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)).thenReturn(Optional.of(latestPlan));
        when(pathRepository.findByPlanAndType(latestPlan, TodoType.IN_PROGRESS)).thenReturn(Collections.emptyList());

        // 마지막으로, 카테고리 조회 시 Optional.empty() 반환
        when(categoryRepository.findByName("NON_EXIST_CAT")).thenReturn(Optional.empty());

        // When & Then: CategoryNotFoundException이 발생해야 함.
        assertThrows(CategoryNotFoundException.class, () -> todoService.modifyTodo(identifier, request));
    }
    // =========================================================
    // removeTodo 메서드 테스트
    // =========================================================

    @Test
    @DisplayName("removeTodo 성공: 유효한 회원과 todo가 존재하면 todo 삭제 성공")
    void givenValidInputs_whenRemoveTodo_thenSuccess() {
        Long memberId = 1L;
        Long todoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        Member member = createMember(memberId, Status.STOP);
        // 삭제할 todo는 타입이 SAVE여야 함.
        Todo todo = Todo.builder()
                .id(todoId)
                .title("삭제할 할 일")
                .member(member)
                .type(TodoType.SAVE)
                .build();

        // 스케줄 관련: 내일 및 오늘 할 일 관련 부모 관계 제거
        when(todoRepository.findWithScheduleByParentTodoAndScheduleDate(todo, LocalDate.now().plusDays(1)))
                .thenReturn(Optional.empty());
        Schedule todaySchedule = Schedule.of(LocalDate.now(), member);
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(todaySchedule));
        when(todoRepository.findByParentTodoAndSchedule(todo, todaySchedule)).thenReturn(Optional.empty());
        // 경로 관련: plan 및 pathRepository stubbing (빈 리스트로 간주)
        Plan latestPlan = createPlan(10L);
        when(planRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId))
                .thenReturn(Optional.of(latestPlan));
        when(pathRepository.findByPlanAndType(latestPlan, TodoType.IN_PROGRESS))
                .thenReturn(Collections.emptyList());

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        TodoManageResult<?> result = todoService.removeTodo(identifier);
        assertNotNull(result);
        assertNull(result.info());
        verify(todoRepository).delete(todo);
    }

    @Test
    @DisplayName("removeTodo 실패: 회원 미존재 시 MemberNotFoundException 발생")
    void givenNonExistingMember_whenRemoveTodo_thenThrowMemberNotFoundException() {
        Long memberId = 1L;
        Long todoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class, () -> todoService.removeTodo(identifier));
    }

    @Test
    @DisplayName("removeTodo 실패: 삭제할 todo 미존재 시 TodoNotFoundException 발생")
    void givenNonExistingTodo_whenRemoveTodo_thenThrowTodoNotFoundException() {
        Long memberId = 1L;
        Long todoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        Member member = createMember(memberId, Status.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());
        assertThrows(TodoNotFoundException.class, () -> todoService.removeTodo(identifier));
    }

    @Test
    @DisplayName("removeTodo 실패: todo 타입 불일치 시 TodoTypeMismatchException 발생")
    void givenTodoTypeMismatch_whenRemoveTodo_thenThrowTodoTypeMismatchException() {
        Long memberId = 1L;
        Long todoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        Member member = createMember(memberId, Status.STOP);
        // 삭제할 todo 타입이 SAVE가 아닌 경우
        Todo todo = Todo.builder()
                .id(todoId)
                .title("삭제할 할 일")
                .member(member)
                .type(TodoType.SCHEDULED)
                .build();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        assertThrows(TodoTypeMismatchException.class, () -> todoService.removeTodo(identifier));
    }
}