package com.dubu.backend.todo.application.impl.today;

import com.dubu.backend.auth.domain.OauthProvider;
import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.Role;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.exception.InvalidMemberStatusException;
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
import com.dubu.backend.todo.exception.*;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.ScheduleRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodayTodoManagementServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private TodayTodoManagementService todoService;

    private Member createMember(Long memberId, Status status) {
        return Member.builder()
                .id(memberId)
                .nickname("testUser")
                .email("test@example.com")
                .oauthProvider(OauthProvider.KAKAO)
                .oauthProviderId("kako123")
                .role(Role.USER)
                .status(status)
                .build();
    }
    // 헬퍼: Category 객체 생성
    private Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .build();
    }

    // 성공 케이스
    @Test
    @DisplayName("성공 케이스: 올바른 회원, 카테고리, 스케줄이 존재하고 todo 제한 미만일 경우 todo 생성 성공")
    void givenValidInputs_whenCreateTodo_thenSuccess() {
        // Given
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        String categoryName = "WORK";
        // TodoCreateRequest 생성 (필요한 필드만 예시로 전달)
        TodoCreateRequest request = new TodoCreateRequest("Test Todo", categoryName, "EASY", "메모 내용");

        Member member = createMember(memberId, Status.STOP);
        Category category = createCategory(categoryName);
        Schedule schedule = Schedule.of(LocalDate.now(), member);
        // 오늘 등록된 todo가 0개라고 가정
        schedule.getTodos().clear();

        // toEntity() 호출 시 Todo 객체 생성: request 내부 로직에 따라 생성되므로, 동일 객체를 반환하도록 stub
        Todo todo = request.toEntity(member, category, schedule, null, TodoType.SCHEDULED);

        // Mockito stubbing
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(schedule));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // When
        TodoManageResult<?> result = todoService.createTodo(identifier, request);

        // Then
        assertNotNull(result);
        assertNotNull(result.info());
    }

    // 실패 케이스 1: 회원 미존재
    @Test
    @DisplayName("실패 케이스: 회원이 존재하지 않을 경우 MemberNotFoundException 발생")
    void givenNonExistingMember_whenCreateTodo_thenThrowMemberNotFoundException() {
        // Given
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        TodoCreateRequest request = new TodoCreateRequest("Test Todo", "WORK", "EASY", "메모 내용");

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MemberNotFoundException.class, () -> todoService.createTodo(identifier, request));
    }

    // 실패 케이스 2: 회원 상태가 STOP이 아닐 경우
    @Test
    @DisplayName("실패 케이스: 회원 상태가 STOP이 아닐 경우 InvalidMemberStatusException 발생")
    void givenMemberNotStopped_whenCreateTodo_thenThrowInvalidMemberStatusException() {
        // Given
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        TodoCreateRequest request = new TodoCreateRequest("Test Todo", "WORK", "EASY", "메모 내용");

        Member member = createMember(memberId, Status.ONBOARDING); // STOP이 아님
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When & Then
        assertThrows(InvalidMemberStatusException.class, () -> todoService.createTodo(identifier, request));
    }

    // 실패 케이스 3: 카테고리 미존재
    @Test
    @DisplayName("실패 케이스: 카테고리가 존재하지 않을 경우 CategoryNotFoundException 발생")
    void givenNonExistingCategory_whenCreateTodo_thenThrowCategoryNotFoundException() {
        // Given
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        String categoryName = "WORK";
        TodoCreateRequest request = new TodoCreateRequest("Test Todo", categoryName, "EASY", "메모 내용");

        Member member = createMember(memberId, Status.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CategoryNotFoundException.class, () -> todoService.createTodo(identifier, request));
    }

    // 실패 케이스 4: 최신 스케줄 미존재
    @Test
    @DisplayName("실패 케이스: 최신 스케줄이 존재하지 않을 경우 ScheduleNotFoundException 발생")
    void givenNoSchedule_whenCreateTodo_thenThrowScheduleNotFoundException() {
        // Given
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        String categoryName = "WORK";
        TodoCreateRequest request = new TodoCreateRequest("Test Todo", categoryName, "EASY", "메모 내용");

        Member member = createMember(memberId, Status.STOP);
        Category category = createCategory(categoryName);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ScheduleNotFoundException.class, () -> todoService.createTodo(identifier, request));
    }

    // 실패 케이스 5: 스케줄에 이미 5개 이상의 todo가 존재하는 경우
    @Test
    @DisplayName("실패 케이스: 스케줄에 등록된 todo가 5개 이상일 경우 TodoLimitExceededException 발생")
    void givenScheduleWithMaxTodos_whenCreateTodo_thenThrowTodoLimitExceededException() {
        // Given
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        String categoryName = "WORK";
        TodoCreateRequest request = new TodoCreateRequest("Test Todo", categoryName, "EASY", "메모 내용");

        Member member = createMember(memberId, Status.STOP);
        Category category = createCategory(categoryName);
        Schedule schedule = Schedule.of(LocalDate.now(), member);
        // 스케줄에 이미 5개의 todo 추가
        for (int i = 0; i < 5; i++) {
            schedule.getTodos().add(Todo.builder().build());
        }

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(schedule));

        // When & Then
        assertThrows(TodoLimitExceededException.class, () -> todoService.createTodo(identifier, request));
    }

    @Test
    @DisplayName("성공 케이스: 올바른 회원 상태와 스케줄, 부모 todo가 존재하고, 동일 아카이브 할 일이 추가되지 않은 경우")
    void givenValidInputs_whenCreateTodoFromArchived_thenSuccess() {
        // Given
        Long memberId = 1L;
        Long parentTodoId = 9999L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, null);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, Status.STOP);
        // Schedule 생성 시 Schedule.of() 메서드를 활용
        Schedule schedule = Schedule.of(LocalDate.now(), member);
        // 아직 등록된 todo가 없도록 기본 리스트 사용 (Schedule.builderDefault에서 이미 new ArrayList<>())

        Category category = Category.builder()
                .name("ARCHIVE")
                .build();

        Todo parentTodo = Todo.builder()
                .title("Archived Todo")
                .type(TodoType.SCHEDULED)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("부모 메모")
                .member(member)
                .category(category)
                .build();

        // Mockito stubbing
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(schedule));
        when(todoRepository.findWithCategoryById(parentTodoId)).thenReturn(Optional.of(parentTodo));
        // 동일 아카이브 todo가 없는 상태
        when(todoRepository.findByParentTodoAndSchedule(parentTodo, schedule)).thenReturn(Optional.empty());
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TodoManageResult<?> result = todoService.createTodoFromArchived(identifier, request);

        // Then
        assertNotNull(result);
        assertNotNull(result.info());
    }

    @Test
    @DisplayName("실패 케이스: 회원이 존재하지 않을 경우 MemberNotFoundException 발생")
    void givenNonExistingMember_whenCreateTodoFromArchived_thenThrowMemberNotFoundException() {
        // Given
        Long memberId = 1L;
        Long parentTodoId = 9999L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, null);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MemberNotFoundException.class, () -> todoService.createTodoFromArchived(identifier, request));
    }

    @Test
    @DisplayName("실패 케이스: 회원 상태가 STOP이 아닐 경우 InvalidMemberStatusException 발생")
    void givenMemberNotStopped_whenCreateTodoFromArchived_thenThrowInvalidMemberStatusException() {
        // Given
        Long memberId = 1L;
        Long parentTodoId = 9999L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, null);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, Status.ONBOARDING); // STOP이 아닌 상태
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When & Then
        assertThrows(InvalidMemberStatusException.class, () -> todoService.createTodoFromArchived(identifier, request));
    }

    @Test
    @DisplayName("실패 케이스: 최신 스케줄을 찾지 못한 경우 ScheduleNotFoundException 발생")
    void givenNoSchedule_whenCreateTodoFromArchived_thenThrowScheduleNotFoundException() {
        // Given
        Long memberId = 1L;
        Long parentTodoId = 9999L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, null);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, Status.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ScheduleNotFoundException.class, () -> todoService.createTodoFromArchived(identifier, request));
    }

    @Test
    @DisplayName("실패 케이스: 스케줄에 이미 5개 이상의 todo가 존재하는 경우 TodoLimitExceededException 발생")
    void givenScheduleWithMaxTodos_whenCreateTodoFromArchived_thenThrowTodoLimitExceededException() {
        // Given
        Long memberId = 1L;
        Long parentTodoId = 9999L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, null);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, Status.STOP);
        // Schedule 생성 후, todos 리스트에 5개의 Todo 추가하여 제한 초과 상황 구성
        Schedule schedule = Schedule.of(LocalDate.now(), member);
        List<Todo> todos = schedule.getTodos();
        for (int i = 0; i < 5; i++) {
            todos.add(Todo.builder().build());
        }

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(schedule));

        // When & Then
        assertThrows(TodoLimitExceededException.class, () -> todoService.createTodoFromArchived(identifier, request));
    }

    @Test
    @DisplayName("실패 케이스: 부모 할 일이 존재하지 않을 경우 TodoNotFoundException 발생")
    void givenNonExistingParentTodo_whenCreateTodoFromArchived_thenThrowTodoNotFoundException() {
        // Given
        Long memberId = 1L;
        Long parentTodoId = 9999L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, null);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, Status.STOP);
        Schedule schedule = Schedule.of(LocalDate.now(), member);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(schedule));
        when(todoRepository.findWithCategoryById(parentTodoId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TodoNotFoundException.class, () -> todoService.createTodoFromArchived(identifier, request));
    }
    @Test
    @DisplayName("실패 케이스: 동일한 아카이브 todo가 이미 추가된 경우 AlreadyAddedTodoFromArchivedException 발생")
    void givenAlreadyAddedArchivedTodo_whenCreateTodoFromArchived_thenThrowAlreadyAddedTodoFromArchivedException() {
        // Given
        Long memberId = 1L;
        Long parentTodoId = 9999L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, null);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, Status.STOP);
        Schedule schedule = Schedule.of(LocalDate.now(), member);
        Category category = createCategory("ARCHIVE");

        Todo parentTodo = Todo.builder()
                .title("Archived Todo")
                .type(TodoType.SCHEDULED)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("부모 메모")
                .member(member)
                .category(category)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(schedule));
        when(todoRepository.findWithCategoryById(parentTodoId)).thenReturn(Optional.of(parentTodo));
        // 이미 동일한 아카이브 할 일이 존재하는 경우
        when(todoRepository.findByParentTodoAndSchedule(parentTodo, schedule)).thenReturn(Optional.of(Todo.builder().build()));

        // When & Then
        assertThrows(AlreadyAddedTodoFromArchivedException.class, () -> todoService.createTodoFromArchived(identifier, request));
    }
    // 성공 케이스: modifyTodo 정상 수정
    @Test
    @DisplayName("성공 케이스: 올바른 회원과 todo, 그리고 수정 요청 내용이 주어지면 todo 수정 성공")
    void givenValidInputs_whenModifyTodo_thenSuccess() {
        // Given
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        // 수정 요청: 제목, 카테고리, 난이도, 메모 (카테고리 업데이트 시 신규 카테고리 존재해야 함)
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NEW_CAT", "HARD", "새 메모");

        Member member = createMember(memberId, Status.STOP);
        Category oldCategory = createCategory("OLD_CAT");
        // 기존 Todo는 SCHEDULED 타입이어야 함.
        Todo todo = Todo.builder()
                .title("기존 제목")
                .category(oldCategory)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("기존 메모")
                .member(member)
                .type(TodoType.SCHEDULED)
                .build();

        Category newCategory = createCategory("NEW_CAT");

        // Mockito stubbing
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(todoId)).thenReturn(Optional.of(todo));
        when(categoryRepository.findByName("NEW_CAT")).thenReturn(Optional.of(newCategory));
        // 수정 시, todo 객체 내부에서 updateTodo()가 호출되어 값이 변경됨.
        // 테스트에서는 결과 객체에 업데이트된 내용이 반영되었는지 확인.

        // When
        TodoManageResult<?> result = todoService.modifyTodo(identifier, request);

        // Then
        assertNotNull(result);
        TodoInfo info = (TodoInfo) result.info();
        assertNotNull(info);
        // 요청한 제목, 카테고리, 난이도, 메모가 반영되었는지 확인 (단, updateTodo 메서드 내부 로직에 따라 값이 변경됨)
        assertEquals("새 제목", info.title());
        assertEquals("NEW_CAT", info.category());
        assertEquals("HARD", info.difficulty());
        assertEquals("새 메모", info.memo());
    }

    // 실패 케이스 1: 회원 미존재
    @Test
    @DisplayName("실패 케이스: 회원이 존재하지 않으면 MemberNotFoundException 발생")
    void givenNonExistingMember_whenModifyTodo_thenThrowMemberNotFoundException() {
        // Given
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NEW_CAT", "HARD", "새 메모");

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MemberNotFoundException.class, () -> todoService.modifyTodo(identifier, request));
    }

    // 실패 케이스 2: 회원 상태가 STOP이 아닌 경우
    @Test
    @DisplayName("실패 케이스: 회원 상태가 STOP이 아니면 InvalidMemberStatusException 발생")
    void givenMemberNotStopped_whenModifyTodo_thenThrowInvalidMemberStatusException() {
        // Given
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NEW_CAT", "HARD", "새 메모");

        Member member = createMember(memberId, Status.ONBOARDING); // STOP 상태 아님
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When & Then
        assertThrows(InvalidMemberStatusException.class, () -> todoService.modifyTodo(identifier, request));
    }

    // 실패 케이스 3: todo 미존재
    @Test
    @DisplayName("실패 케이스: 수정할 todo가 존재하지 않으면 TodoNotFoundException 발생")
    void givenNonExistingTodo_whenModifyTodo_thenThrowTodoNotFoundException() {
        // Given
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NEW_CAT", "HARD", "새 메모");

        Member member = createMember(memberId, Status.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(todoId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TodoNotFoundException.class, () -> todoService.modifyTodo(identifier, request));
    }

    // 실패 케이스 4: todo 타입 불일치 (예: 기존 todo가 SCHEDULED 타입이 아닌 경우)
    @Test
    @DisplayName("실패 케이스: todo 타입이 SCHEDULED가 아니면 TodoTypeMismatchException 발생")
    void givenTodoTypeMismatch_whenModifyTodo_thenThrowTodoTypeMismatchException() {
        // Given
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NEW_CAT", "HARD", "새 메모");

        Member member = createMember(memberId, Status.STOP);
        // 기존 todo의 타입을 SCHEDULED가 아닌 다른 타입으로 설정
        Todo todo = Todo.builder()
                .title("기존 제목")
                .member(member)
                .type(TodoType.valueOf("SAVE")) // 예시로 ARCHIVED 타입 사용
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(todoId)).thenReturn(Optional.of(todo));

        // When & Then
        assertThrows(
                com.dubu.backend.todo.exception.TodoTypeMismatchException.class,
                () -> todoService.modifyTodo(identifier, request)
        );
    }

    // 실패 케이스 5: 수정 요청에 포함된 카테고리 정보가 존재하지 않을 경우
    @Test
    @DisplayName("실패 케이스: 수정 요청 카테고리가 존재하지 않으면 CategoryNotFoundException 발생")
    void givenNonExistingCategory_whenModifyTodo_thenThrowCategoryNotFoundException() {
        // Given
        Long memberId = 1L;
        Long todoId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        // 수정 요청 시 카테고리 업데이트 요청
        TodoUpdateRequest request = new TodoUpdateRequest("새 제목", "NON_EXIST_CAT", "HARD", "새 메모");

        Member member = createMember(memberId, Status.STOP);
        Category oldCategory = createCategory("OLD_CAT");
        Todo todo = Todo.builder()
                .title("기존 제목")
                .category(oldCategory)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("기존 메모")
                .member(member)
                .type(TodoType.SCHEDULED)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(todoId)).thenReturn(Optional.of(todo));
        // 수정 요청에 따른 신규 카테고리가 존재하지 않음
        when(categoryRepository.findByName("NON_EXIST_CAT")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                CategoryNotFoundException.class,
                () -> todoService.modifyTodo(identifier, request)
        );
    }

    // -----------------------------------------------------
    // removeTodo 메서드 테스트
    // -----------------------------------------------------
    // 성공 케이스: 정상 삭제 시 결과가 반환되어야 함 (todoInfo는 null)
    @Test
    @DisplayName("성공 케이스: 올바른 회원과 todo가 존재하면 todo 삭제 성공")
    void givenValidInputs_whenRemoveTodo_thenSuccess() {
        // Given
        Long memberId = 1L;
        Long todoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);

        Member member = createMember(memberId, Status.STOP);
        // 삭제할 todo는 SCHEDULED 타입이어야 함.
        Todo todo = Todo.builder()
                .title("삭제할 할 일")
                .member(member)
                .type(TodoType.SCHEDULED)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        // When
        TodoManageResult<?> result = todoService.removeTodo(identifier);

        // Then
        // 삭제 후 리턴되는 TodoManageResult의 todoInfo는 null 이어야 함.
        assertNotNull(result);
        assertNull(result.info());
        // 실제로 삭제가 호출되었는지도 검증할 수 있음.
        verify(todoRepository).delete(todo);
    }

    // 실패 케이스 1: 회원 미존재 (remove)
    @Test
    @DisplayName("실패 케이스: 회원이 존재하지 않으면 removeTodo에서 MemberNotFoundException 발생")
    void givenNonExistingMember_whenRemoveTodo_thenThrowMemberNotFoundException() {
        // Given
        Long memberId = 1L;
        Long todoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MemberNotFoundException.class, () -> todoService.removeTodo(identifier));
    }

    // 실패 케이스 2: 회원 상태 불일치 (remove)
    @Test
    @DisplayName("실패 케이스: 회원 상태가 STOP이 아니면 removeTodo에서 InvalidMemberStatusException 발생")
    void givenMemberNotStopped_whenRemoveTodo_thenThrowInvalidMemberStatusException() {
        // Given
        Long memberId = 1L;
        Long todoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);

        Member member = createMember(memberId, Status.ONBOARDING);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When & Then
        assertThrows(InvalidMemberStatusException.class, () -> todoService.removeTodo(identifier));
    }

    // 실패 케이스 3: 삭제할 todo 미존재
    @Test
    @DisplayName("실패 케이스: 삭제할 todo가 존재하지 않으면 TodoNotFoundException 발생")
    void givenNonExistingTodo_whenRemoveTodo_thenThrowTodoNotFoundException() {
        // Given
        Long memberId = 1L;
        Long todoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);

        Member member = createMember(memberId, Status.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TodoNotFoundException.class, () -> todoService.removeTodo(identifier));
    }

    // 실패 케이스 4: todo 타입 불일치 (remove)
    @Test
    @DisplayName("실패 케이스: 삭제할 todo의 타입이 SCHEDULED가 아니면 TodoTypeMismatchException 발생")
    void givenTodoTypeMismatch_whenRemoveTodo_thenThrowTodoTypeMismatchException() {
        // Given
        Long memberId = 1L;
        Long todoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);

        Member member = createMember(memberId, Status.STOP);
        // 삭제할 todo의 타입이 SCHEDULED가 아닌 경우
        Todo todo = Todo.builder()
                .title("삭제할 할 일")
                .member(member)
                .type(TodoType.valueOf("SAVE"))
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        // When & Then
        assertThrows(
                com.dubu.backend.todo.exception.TodoTypeMismatchException.class,
                () -> todoService.removeTodo(identifier)
        );
    }
}