package com.dubu.backend.todo.application.impl.path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.dubu.backend.core.domain.PageResponse;
import com.dubu.backend.member.domain.model.Member;
import com.dubu.backend.member.domain.enums.MemberStatus;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberCategoryRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.member.core.exception.InvalidMemberStatusException;
import com.dubu.backend.plan.core.exception.PathNotFoundException;
import com.dubu.backend.plan.domain.repository.SubPathRepository;
import com.dubu.backend.todo.domain.past.Category;
import com.dubu.backend.todo.domain.past.Todo;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.dto.common.Cursor;
import com.dubu.backend.todo.dto.common.TodoIdentifier;
import com.dubu.backend.todo.dto.response.TodoInfo;
import com.dubu.backend.todo.dto.search.TodoSearchCond;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class SubPathTodoQueryServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private MemberCategoryRepository memberCategoryRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TodoRepository todoRepository;
    @Mock private SubPathRepository subPathRepository;
    @Mock private TodoRandomSelector todoRandomSelector;

    @InjectMocks
    private PathTodoQueryService service;

    // 헬퍼 메서드: MemberEntity 생성 (MOVE 상태여야 정상 로직)
    private Member createMember(Long memberId, MemberStatus status) {
        return Member.builder()
                .id(memberId)
                .nickname("testUser")
                .email("test@example.com")
                .status(status)
                .build();
    }

    private SubPath createPath(Long pathId) {
        SubPath subPath = SubPath.builder()
                .id(pathId)
                .build();
        // Path 엔티티 내부 todos는 @Builder.Default로 ArrayList 초기화
        return subPath;
    }

    // CategoryEntity 헬퍼
    private Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .build();
    }

    // Todo 헬퍼 (cursorDifficulty 반드시 설정)
    private Todo createTodo(Long id, String title, Member member, Category category, SubPath subPath, TodoType type) {
        return Todo.builder()
                .id(id)
                .title(title)
                .member(member)
                .category(category)
                .subPath(subPath)
                .type(type)
                .difficulty(TodoDifficulty.NORMAL) // NPE 방지
                .memo("Memo " + id)
                .build();
    }

    // ----------------------------------------------------------
    // findTargetTodos 테스트
    // ----------------------------------------------------------
    @Test
    @DisplayName("findTargetTodos 성공: 회원 상태 MOVE, path 존재 -> 할 일 목록 반환")
    void findTargetTodos_success() {
        Long memberId = 1L;
        Long pathId = 100L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, pathId);

        Member member = createMember(memberId, MemberStatus.MOVE);
        SubPath subPath = createPath(pathId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(subPathRepository.findById(pathId)).thenReturn(Optional.of(subPath));

        // Path에 연결된 Todo 2건
        Category cat = createCategory("TEST");
        Todo todo1 = createTodo(10L, "Path Todo 1", member, cat, subPath, TodoType.IN_PROGRESS);
        Todo todo2 = createTodo(11L, "Path Todo 2", member, cat, subPath, TodoType.IN_PROGRESS);
        List<Todo> todos = List.of(todo1, todo2);
        when(todoRepository.findTodosWithCategoryByPath(subPath)).thenReturn(todos);

        // 실행
        List<TodoInfo> result = service.findTargetTodos(identifier);

        // 검증
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Path Todo 1", result.get(0).title());
        assertEquals("Path Todo 2", result.get(1).title());
    }

    @Test
    @DisplayName("findTargetTodos 실패: 회원 미존재 -> MemberNotFoundException")
    void findTargetTodos_fail_memberNotFound() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, 100L);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class, () -> service.findTargetTodos(identifier));
    }

    @Test
    @DisplayName("findTargetTodos 실패: 회원 상태가 MOVE가 아님 -> InvalidMemberStatusException")
    void findTargetTodos_fail_invalidMemberStatus() {
        Long memberId = 1L;
        Member member = createMember(memberId, MemberStatus.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        TodoIdentifier identifier = new TodoIdentifier(memberId, null, 100L);
        assertThrows(InvalidMemberStatusException.class, () -> service.findTargetTodos(identifier));
    }

    @Test
    @DisplayName("findTargetTodos 실패: path 미존재 -> PathNotFoundException")
    void findTargetTodos_fail_pathNotFound() {
        Long memberId = 1L;
        Member member = createMember(memberId, MemberStatus.MOVE);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        Long pathId = 999L;
        when(subPathRepository.findById(pathId)).thenReturn(Optional.empty());

        TodoIdentifier identifier = new TodoIdentifier(memberId, null, pathId);
        assertThrows(PathNotFoundException.class, () -> service.findTargetTodos(identifier));
    }

    // ----------------------------------------------------------
    // findSaveTodos 테스트
    // ----------------------------------------------------------
    @Test
    @DisplayName("findSaveTodos 성공: 회원 상태 MOVE, path 존재 -> 저장된 할 일 페이징 반환")
    void findSaveTodos_success() {
        Long memberId = 1L;
        Long pathId = 10L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, pathId);

        Member member = createMember(memberId, MemberStatus.MOVE);
        SubPath subPath = createPath(pathId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(subPathRepository.findById(pathId)).thenReturn(Optional.of(subPath));

        SaveTodoQueryRequest request = new SaveTodoQueryRequest(5);
        // slice stub
        Category cat = createCategory("SAVE_CAT");
        Todo todo1 = createTodo(20L, "Save Todo 1", member, cat, subPath, TodoType.SAVE);
        Todo todo2 = createTodo(21L, "Save Todo 2", member, cat, subPath, TodoType.SAVE);
        List<Todo> todos = List.of(todo1, todo2);
        Slice<Todo> fakeSlice = new SliceImpl<>(todos, PageRequest.ofSize(request.size()), false);

        when(todoRepository.findTodosUsingSingleCursor(
                null,
                TodoSearchCond.builder().member(member).type(TodoType.SAVE).build(),
                PageRequest.ofSize(request.size())))
                .thenReturn(fakeSlice);

        // stubbing: path의 부모 todo ID 목록
        when(todoRepository.findParentTodoIdsByPathAndParentTodoNotNull(subPath))
                .thenReturn(List.of(30L));

        PageResponse<Long, List<TodoInfo>> response =
                service.findSaveTodos(identifier, null, request);

        assertNotNull(response);
        assertFalse(response.hasNext());
        assertNotNull(response.data());
        assertEquals(2, response.data().size());
        assertEquals("Save Todo 1", response.data().get(0).title());
        assertEquals("Save Todo 2", response.data().get(1).title());
    }

    // ----------------------------------------------------------
    // findPersonalizedRecommendTodos 테스트
    // ----------------------------------------------------------
    @Test
    @DisplayName("findPersonalizedRecommendTodos 성공: 회원 MOVE, path 존재 -> 개인 맞춤 추천")
    void findPersonalizedRecommendTodos_success() {
        Long memberId = 1L;
        Long pathId = 10L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, pathId);

        Member member = createMember(memberId, MemberStatus.MOVE);
        SubPath subPath = createPath(pathId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(subPathRepository.findById(pathId)).thenReturn(Optional.of(subPath));

        // 카테고리
        when(memberCategoryRepository.findCategoryIdsByMember(member))
                .thenReturn(List.of(100L));
        when(todoRepository.findTodosWithCategoryByCategoryIdsAndType(List.of(100L), TodoType.RECOMMEND))
                .thenReturn(List.of(200L, 201L));

        when(todoRandomSelector.selectTodos(5, List.of(200L, 201L))).thenReturn(List.of(200L));
        Category cat = createCategory("RECOMMEND");
        Todo recommended = createTodo(200L, "Path Recommend", member, cat, subPath, TodoType.RECOMMEND);
        when(todoRepository.findAllById(List.of(200L))).thenReturn(List.of(recommended));

        // path의 부모 todo
        when(todoRepository.findParentTodoIdsByPathAndParentTodoNotNull(subPath))
                .thenReturn(List.of(30L));
        when(todoRepository.findParentTodoIdsByIdsAndTypeAndParentTodoNotNull(List.of(30L), TodoType.SAVE))
                .thenReturn(List.of(40L));

        List<TodoInfo> result = service.findPersonalizedRecommendTodos(identifier);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Path Recommend", result.get(0).title());
    }

    // ----------------------------------------------------------
    // findAllRecommendTodos 테스트
    // ----------------------------------------------------------
    @Test
    @DisplayName("findAllRecommendTodos 성공: 회원 MOVE, path 존재 -> compositeCursor로 추천 할 일 페이지 반환")
    void findAllRecommendTodos_success() {
        Long memberId = 1L;
        Long pathId = 10L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, pathId);

        Member member = createMember(memberId, MemberStatus.MOVE);
        SubPath subPath = createPath(pathId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(subPathRepository.findById(pathId)).thenReturn(Optional.of(subPath));

        // 요청 DTO
        Cursor cursor = Cursor.of(1L, TodoDifficulty.EASY, 10L);
        RecommendTodoQueryRequest request = new RecommendTodoQueryRequest(
                List.of("READING"), // category
                List.of("EASY"),    // cursorDifficulty
                5
        );

        Category readingCat = createCategory("READING");
        when(categoryRepository.findCategoriesByName(List.of("READING")))
                .thenReturn(List.of(readingCat));
        List<TodoDifficulty> diffs = List.of(TodoDifficulty.EASY);

        // slice
        Todo recommended = createTodo(100L, "Path Recommended", member, readingCat, subPath, TodoType.RECOMMEND);
        recommended.updateTodo(null, null, TodoDifficulty.EASY, null); // 안전
        List<Todo> recTodos = List.of(recommended);
        Slice<Todo> fakeSlice = new SliceImpl<>(recTodos, PageRequest.ofSize(5), false);

        when(todoRepository.findTodosUsingCompositeCursor(
                cursor,
                TodoSearchCond.builder()
                        .type(TodoType.RECOMMEND)
                        .categories(List.of(readingCat))
                        .difficulties(diffs)
                        .build(),
                PageRequest.ofSize(5)))
                .thenReturn(fakeSlice);

        when(todoRepository.findParentTodoIdsByPathAndParentTodoNotNull(subPath))
                .thenReturn(List.of(30L));
        when(todoRepository.findParentTodoIdsByIdsAndTypeAndParentTodoNotNull(List.of(30L), TodoType.SAVE))
                .thenReturn(List.of(40L));

        PageResponse<Cursor, List<TodoInfo>> response =
                service.findAllRecommendTodos(identifier, cursor, request);

        assertNotNull(response);
        assertFalse(response.hasNext());
        assertNotNull(response.nextCursor());
        List<TodoInfo> infos = response.data();
        assertEquals(1, infos.size());
        assertEquals("Path Recommended", infos.get(0).title());
    }

    // -------------------------------------------------------------------
    // 공통 실패 케이스 (회원 미존재, 상태 != MOVE, path 미존재) 생략 or 중복 가능
    // -------------------------------------------------------------------
}