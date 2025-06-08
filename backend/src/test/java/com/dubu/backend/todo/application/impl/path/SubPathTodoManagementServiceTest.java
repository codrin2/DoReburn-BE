package com.dubu.backend.todo.application.impl.path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dubu.backend.member.domain.enums.MemberStatus;
import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.plan.domain.enums.TrafficType;
import com.dubu.backend.member.core.exception.InvalidMemberStatusException;
import com.dubu.backend.plan.core.exception.PathNotFoundException;
import com.dubu.backend.plan.domain.repository.SubPathRepository;
import com.dubu.backend.todo.domain.past.Category;
import com.dubu.backend.todo.domain.past.Todo;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.dto.common.TodoIdentifier;
import com.dubu.backend.todo.dto.response.TodoInfo;
import com.dubu.backend.todo.core.exception.TodoCountExceededException;
import com.dubu.backend.todo.exception.TodoNotFoundException;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubPathTodoManagementServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private TodoRepository todoRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private SubPathRepository subPathRepository;

    @InjectMocks
    private PathTodoManagementService service;

    // 헬퍼 메서드: MemberEntity 생성 (PathTodoManagementService는 회원 상태가 MOVE여야 함)
    private Member createMember(Long memberId, MemberStatus status) {
        return Member.builder()
                .id(memberId)
                .nickname("testUser")
                .email("test@example.com")
                .oauthProvider(null) // 필요에 따라 설정
                .oauthProviderId("providerId")
                .role(null) // 필요에 따라 설정
                .status(status)
                .build();
    }

    // 헬퍼 메서드: CategoryEntity 생성
    private Category createCategory(String name) {
        return Category.builder().name(name).build();
    }
    private SubPath createPath(Long pathId) {
        return SubPath.builder()
                .id(pathId)
                .trafficType(TrafficType.BUS)  // 예시: 적절한 TrafficType 사용
                .subwayCode(0)
                .busNumber("0")
                .busType(0)
                .startName("Start")
                .endName("End")
                .sectionTime(10)
                .pathOrder(1)
                .build();
    }

    private SubPath createPathWithTodo(Long pathId, Todo todo) {
        SubPath subPath = createPath(pathId);
        subPath.getTodos().add(todo);
        return subPath;
    }

    // 헬퍼 메서드: Todo 생성 (예시)
    private Todo createTodo(Long todoId, Member member, Category category, SubPath subPath, TodoType type) {
        return Todo.builder()
                .id(todoId)
                .title("기존 제목")
                .category(category)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("기존 메모")
                .member(member)
                .type(type)
                .build();
    }

    // =========================================================
    // createTodo 테스트
    // =========================================================

    @Test
    @DisplayName("createTodo 성공: 이동 중 회원, 경로, 카테고리 존재 및 할 일 수 제한 미달일 경우 할 일 생성")
    void createTodo_success() {
        Long memberId = 1L;
        Long pathId = 10L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, pathId);
        TodoCreateRequest request = new TodoCreateRequest("할 일 제목", "CATEGORY", "EASY", "메모 내용");

        Member member = createMember(memberId, MemberStatus.MOVE);
        Category category = createCategory("CATEGORY");
        SubPath subPath = createPath(pathId);
        List<Todo> todosByPath = new ArrayList<>(); // 현재 등록된 할 일 없음

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(subPathRepository.findById(pathId)).thenReturn(Optional.of(subPath));
        when(categoryRepository.findByName("CATEGORY")).thenReturn(Optional.of(category));
        when(todoRepository.findTodosByPath(subPath)).thenReturn(todosByPath);

        // DTO 내부 toEntity()를 통해 생성된 Todo (IN_PROGRESS 타입)
        Todo todo = request.toEntity(member, category, null, subPath, TodoType.IN_PROGRESS);
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoManageResult<?> result = service.createTodo(identifier, request);
        assertNotNull(result);
        TodoInfo info = (TodoInfo) result.info();
        assertEquals("할 일 제목", info.title());
    }

    @Test
    @DisplayName("createTodo 실패: 회원 미존재 시 MemberNotFoundException 발생")
    void createTodo_fail_memberNotFound() {
        Long memberId = 1L;
        Long pathId = 10L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, pathId);
        TodoCreateRequest request = new TodoCreateRequest("할 일 제목", "CATEGORY", "EASY", "메모 내용");

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class, () -> service.createTodo(identifier, request));
    }

    @Test
    @DisplayName("createTodo 실패: 회원 상태가 MOVE가 아닐 경우 InvalidMemberStatusException 발생")
    void createTodo_fail_invalidMemberStatus() {
        Long memberId = 1L;
        Long pathId = 10L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, pathId);
        TodoCreateRequest request = new TodoCreateRequest("할 일 제목", "CATEGORY", "EASY", "메모 내용");

        Member member = createMember(memberId, MemberStatus.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        assertThrows(InvalidMemberStatusException.class, () -> service.createTodo(identifier, request));
    }

    @Test
    @DisplayName("createTodo 실패: 경로 미존재 시 PathNotFoundException 발생")
    void createTodo_fail_pathNotFound() {
        Long memberId = 1L;
        Long pathId = 10L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, pathId);
        TodoCreateRequest request = new TodoCreateRequest("할 일 제목", "CATEGORY", "EASY", "메모 내용");

        Member member = createMember(memberId, MemberStatus.MOVE);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(subPathRepository.findById(pathId)).thenReturn(Optional.empty());
        assertThrows(PathNotFoundException.class, () -> service.createTodo(identifier, request));
    }

    @Test
    @DisplayName("createTodo 실패: 경로에 할 일이 10개이면 TodoLimitExceededException 발생")
    void createTodo_fail_todoLimitExceeded() {
        Long memberId = 1L;
        Long pathId = 10L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, pathId);
        TodoCreateRequest request = new TodoCreateRequest("할 일 제목", "CATEGORY", "EASY", "메모 내용");

        Member member = createMember(memberId, MemberStatus.MOVE);
        Category category = createCategory("CATEGORY");
        SubPath subPath = createPath(pathId);
        // 이미 10건의 할 일이 등록되어 있다고 가정
        List<Todo> todosByPath = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            todosByPath.add(Todo.builder().build());
        }
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(subPathRepository.findById(pathId)).thenReturn(Optional.of(subPath));
        when(categoryRepository.findByName("CATEGORY")).thenReturn(Optional.of(category));
        when(todoRepository.findTodosByPath(subPath)).thenReturn(todosByPath);
        assertThrows(TodoCountExceededException.class, () -> service.createTodo(identifier, request));
    }

    // =========================================================
    // createTodoFromArchived 테스트
    // =========================================================

    @Test
    @DisplayName("createTodoFromArchived 성공: 부모 할 일이 존재하고, 동일 아카이브 할 일이 없으면 할 일 생성")
    void createTodoFromArchived_success() {
        Long memberId = 1L;
        Long pathId = 10L;
        Long parentTodoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, pathId);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, MemberStatus.MOVE);
        Category category = createCategory("ARCHIVE");
        SubPath subPath = createPath(pathId);
        // 부모 할 일
        Todo parentTodo = Todo.builder()
                .id(parentTodoId)
                .title("부모 할 일")
                .difficulty(TodoDifficulty.NORMAL)
                .memo("부모 메모")
                .member(member)
                .category(category)
                .type(TodoType.IN_PROGRESS)
                .build();

        // 경로의 할 일 목록 (현재 0건)
        List<Todo> todosByPath = new ArrayList<>();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(parentTodoId)).thenReturn(Optional.of(parentTodo));
        when(subPathRepository.findById(pathId)).thenReturn(Optional.of(subPath));
        when(todoRepository.findTodosByPath(subPath)).thenReturn(todosByPath);
        // 이미 추가된 할 일이 없음을 확인
        when(todoRepository.findByParentTodoAndPath(parentTodo, subPath)).thenReturn(Optional.empty());
        // 새 할 일 생성
        Todo newTodo = Todo.of(parentTodo.getTitle(), TodoType.IN_PROGRESS, parentTodo.getDifficulty(),
                parentTodo.getMemo(), member, category, parentTodo, null, subPath);
        when(todoRepository.save(any(Todo.class))).thenReturn(newTodo);

        TodoManageResult<?> result = service.createTodoFromArchived(identifier, request);
        assertNotNull(result);
        TodoInfo info = (TodoInfo) result.info();
        assertEquals("부모 할 일", info.title());
    }

    @Test
    @DisplayName("createTodoFromArchived 실패: 부모 할 일이 미존재하면 TodoNotFoundException 발생")
    void createTodoFromArchived_fail_parentNotFound() {
        Long memberId = 1L;
        Long pathId = 10L;
        Long parentTodoId = 200L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, parentTodoId, pathId);
        TodoCreateFromArchivedRequest request = new TodoCreateFromArchivedRequest(parentTodoId);

        Member member = createMember(memberId, MemberStatus.MOVE);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findWithCategoryById(parentTodoId)).thenReturn(Optional.empty());
        assertThrows(TodoNotFoundException.class, () -> service.createTodoFromArchived(identifier, request));
    }

    // =========================================================
    // modifyTodo 테스트
    // =========================================================

    @Test
    @DisplayName("modifyTodo 성공: 이동 중 회원, 할 일 존재 시 할 일 수정")
    void modifyTodo_success() {
        Long memberId = 1L;
        Long todoId = 300L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoUpdateRequest request = new TodoUpdateRequest("수정 제목", "NEW_CAT", "HARD", "수정 메모");

        Member member = createMember(memberId, MemberStatus.MOVE);
        Category oldCategory = createCategory("OLD_CAT");
        Todo todo = Todo.builder()
                .id(todoId)
                .title("기존 제목")
                .category(oldCategory)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("기존 메모")
                .member(member)
                .type(TodoType.IN_PROGRESS)
                .build();
        Category newCategory = createCategory("NEW_CAT");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(categoryRepository.findByName("NEW_CAT")).thenReturn(Optional.of(newCategory));
        when(todoRepository.findWithCategoryById(todoId)).thenReturn(Optional.of(todo));

        // 수정 후 updateTodo()가 호출되어 내부 값 변경
        todo.updateTodo("수정 제목", newCategory, TodoDifficulty.HARD, "수정 메모");

        TodoManageResult<?> result = service.modifyTodo(identifier, request);
        assertNotNull(result);
        TodoInfo info = (TodoInfo) result.info();
        assertEquals("수정 제목", info.title());
        assertEquals("NEW_CAT", info.category());
        assertEquals("HARD", info.difficulty());
        assertEquals("수정 메모", info.memo());
    }

    // =========================================================
    // removeTodo 테스트
    // =========================================================

    @Test
    @DisplayName("removeTodo 성공: 이동 중 회원, 할 일 존재 시 할 일 삭제")
    void removeTodo_success() {
        Long memberId = 1L;
        Long todoId = 400L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);

        Member member = createMember(memberId, MemberStatus.MOVE);
        Todo todo = createTodo(todoId, member, createCategory("CAT"), createPath(10L), TodoType.IN_PROGRESS);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        TodoManageResult<?> result = service.removeTodo(identifier);
        assertNotNull(result);
        assertNull(result.info());
        verify(todoRepository).delete(todo);
    }

    // =========================================================
    // toggleTodoCompletion 테스트
    // =========================================================

    @Test
    @DisplayName("toggleTodoCompletion 성공: 이동 중 회원, 할 일 완료 상태 토글")
    void toggleTodoCompletion_success() {
        Long memberId = 1L;
        Long todoId = 500L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoCompletionToggleRequest request = new TodoCompletionToggleRequest(true);

        Member member = createMember(memberId, MemberStatus.MOVE);
        Todo todo = createTodo(todoId, member, createCategory("CAT"), createPath(10L), TodoType.IN_PROGRESS);
        // 초기 완료 상태 false
        todo.updateCompletedStatus(false);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        // 실행 후 완료 상태 true로 업데이트
        service.toggleTodoCompletion(identifier, request);
        assertTrue(todo.getIsCompleted());
    }

    // =========================================================
    // modifyTodoPath 테스트
    // =========================================================

    @Test
    @DisplayName("modifyTodoPath 성공: 이동 중 회원, 할 일 존재 시 새로운 경로로 업데이트")
    void modifyTodoPath_success() {
        Long memberId = 1L;
        Long todoId = 600L;
        Long newPathId = 20L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, todoId, null);
        TodoPathUpdateRequest request = new TodoPathUpdateRequest(newPathId);

        Member member = createMember(memberId, MemberStatus.MOVE);
        Todo todo = createTodo(todoId, member, createCategory("CAT"), createPath(10L), TodoType.IN_PROGRESS);
        SubPath newSubPath = createPath(newPathId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(subPathRepository.findById(newPathId)).thenReturn(Optional.of(newSubPath));

        // 실행: 할 일의 경로를 업데이트
        service.modifyTodoPath(identifier, request);
        // todo 객체의 path가 newPath로 변경되었는지 검증 (가정: getPath() 메서드 존재)
        assertEquals(newSubPath, todo.getSubPath());
    }
}