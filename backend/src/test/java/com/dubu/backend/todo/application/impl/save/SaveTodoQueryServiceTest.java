package com.dubu.backend.todo.application.impl.save;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.core.exception.MemberNotFoundException;
import com.dubu.backend.member.domain.repository.MemberCategoryRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.plan.core.exception.InvalidMemberStatusException;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.Todo;
import com.dubu.backend.todo.domain.enums.TodoDifficulty;
import com.dubu.backend.todo.domain.enums.TodoType;
import com.dubu.backend.todo.dto.common.Cursor;
import com.dubu.backend.todo.dto.common.TodoIdentifier;
import com.dubu.backend.todo.dto.request.RecommendTodoQueryRequest;
import com.dubu.backend.todo.dto.request.SaveTodoQueryRequest;
import com.dubu.backend.todo.dto.response.TodoInfo;
import com.dubu.backend.todo.dto.search.TodoSearchCond;
import com.dubu.backend.todo.infra.repository.CategoryRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import com.dubu.backend.todo.application.util.TodoRandomSelector;
import com.dubu.backend.core.domain.PageResponse;
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
class SaveTodoQueryServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private MemberCategoryRepository memberCategoryRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TodoRepository todoRepository;
    @Mock private TodoRandomSelector todoRandomSelector;

    @InjectMocks
    private SaveTodoQueryService service;

    // 헬퍼 메서드: Member 생성
    private Member createMember(Long memberId, Status status) {
        return Member.builder()
                .id(memberId)
                .nickname("testUser")
                .email("test@example.com")
                .status(status)
                .build();
    }

    // 헬퍼 메서드: Category 생성
    private Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .build();
    }

    // 헬퍼 메서드: Todo 생성 (difficulty를 반드시 설정)
    private Todo createTodo(Long id, String title, Member member, Category category, TodoType type) {
        return Todo.builder()
                .id(id)
                .title(title)
                .category(category)
                .member(member)
                .type(type)
                .difficulty(TodoDifficulty.NORMAL) // NullPointerException 방지
                .memo("Memo" + id)
                .build();
    }

    // ================================================================================
    // findSaveTodos 테스트
    // ================================================================================
    @Test
    @DisplayName("findSaveTodos 성공: 회원 상태가 STOP 또는 MOVE이고, 할 일 목록 페이지를 정상 반환")
    void findSaveTodos_success() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        SaveTodoQueryRequest request = new SaveTodoQueryRequest(5);

        // 회원 상태가 STOP (또는 MOVE)
        Member member = createMember(memberId, Status.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // fake Slice 생성
        Category category = createCategory("CAT");
        Todo todo1 = createTodo(10L, "Save Todo 1", member, category, TodoType.SAVE);
        Todo todo2 = createTodo(11L, "Save Todo 2", member, category, TodoType.SAVE);

        List<Todo> todos = List.of(todo1, todo2);
        Slice<Todo> fakeSlice = new SliceImpl<>(todos, PageRequest.ofSize(request.size()), false);

        when(todoRepository.findTodosUsingSingleCursor(
                null,
                TodoSearchCond.builder().member(member).type(TodoType.SAVE).build(),
                PageRequest.ofSize(request.size())
        )).thenReturn(fakeSlice);

        // 실행
        PageResponse<Long, List<TodoInfo>> response = service.findSaveTodos(identifier, null, request);

        // 검증
        assertNotNull(response);
        assertFalse(response.hasNext());
        assertNotNull(response.data());
        assertEquals(2, response.data().size());
        assertEquals("Save Todo 1", response.data().get(0).title());
        assertEquals("Save Todo 2", response.data().get(1).title());
    }

    @Test
    @DisplayName("findSaveTodos 실패: 회원 미존재 시 MemberNotFoundException 발생")
    void findSaveTodos_fail_memberNotFound() {
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        SaveTodoQueryRequest request = new SaveTodoQueryRequest(5);

        assertThrows(MemberNotFoundException.class, () ->
                service.findSaveTodos(new TodoIdentifier(memberId, null, null), null, request));
    }

    @Test
    @DisplayName("findSaveTodos 실패: 회원 상태가 ONBOARDING 또는 FEEDBACK일 때 InvalidMemberStatusException 발생")
    void findSaveTodos_fail_invalidMemberStatus() {
        Long memberId = 1L;
        // ONBOARDING 상태
        Member member = createMember(memberId, Status.ONBOARDING);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        SaveTodoQueryRequest request = new SaveTodoQueryRequest(5);
        assertThrows(InvalidMemberStatusException.class, () ->
                service.findSaveTodos(new TodoIdentifier(memberId, null, null), null, request));
    }

    // ================================================================================
    // findPersonalizedRecommendTodos 테스트
    // ================================================================================
    @Test
    @DisplayName("findPersonalizedRecommendTodos 성공: 회원 상태 STOP/MOVE, 추천 할 일 목록 조회")
    void findPersonalizedRecommendTodos_success() {
        Long memberId = 1L;
        Member member = createMember(memberId, Status.STOP);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // 카테고리 IDs
        when(memberCategoryRepository.findCategoryIdsByMember(member)).thenReturn(List.of(100L, 101L));
        // 추천 할 일 ID 목록
        when(todoRepository.findTodosWithCategoryByCategoryIdsAndType(List.of(100L, 101L), TodoType.RECOMMEND))
                .thenReturn(List.of(200L, 201L));
        // 랜덤 선택된 ID들
        when(todoRandomSelector.selectTodos(5, List.of(200L, 201L))).thenReturn(List.of(200L));

        // ID 기반 Todo 조회
        Category category = createCategory("CAT");
        Todo recommendedTodo = createTodo(200L, "Recommended Todo", member, category, TodoType.RECOMMEND);
        when(todoRepository.findAllById(List.of(200L))).thenReturn(List.of(recommendedTodo));

        // 부모 할 일이 S
        when(todoRepository.findParentTodoIdsByParentTodoAndMemberAndType(List.of(recommendedTodo), member, TodoType.SAVE))
                .thenReturn(List.of()); // 예: 자식이 없는 경우

        List<TodoInfo> result = service.findPersonalizedRecommendTodos(new TodoIdentifier(memberId, null, null));
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Recommended Todo", result.get(0).title());
    }

    @Test
    @DisplayName("findPersonalizedRecommendTodos 실패: 회원 미존재 시 MemberNotFoundException 발생")
    void findPersonalizedRecommendTodos_fail_memberNotFound() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class, () ->
                service.findPersonalizedRecommendTodos(new TodoIdentifier(999L, null, null)));
    }

    @Test
    @DisplayName("findPersonalizedRecommendTodos 실패: 회원 상태가 ONBOARDING 또는 FEEDBACK이면 InvalidMemberStatusException 발생")
    void findPersonalizedRecommendTodos_fail_invalidMemberStatus() {
        Long memberId = 1L;
        Member member = createMember(memberId, Status.ONBOARDING);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        assertThrows(InvalidMemberStatusException.class, () ->
                service.findPersonalizedRecommendTodos(new TodoIdentifier(memberId, null, null)));
    }

    // ================================================================================
    // findAllRecommendTodos 테스트
    // ================================================================================
    @Test
    @DisplayName("findAllRecommendTodos 성공: 회원 상태 STOP/MOVE, compositeCursor로 추천 할 일 페이지 반환")
    void findAllRecommendTodos_success() {
        Long memberId = 1L;
        Member member = createMember(memberId, Status.MOVE); // MOVE도 가능
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // 요청 DTO
        Cursor cursor = Cursor.of(1L, TodoDifficulty.EASY, 10L);
        RecommendTodoQueryRequest request = new RecommendTodoQueryRequest(
                List.of("READING"), List.of("EASY"), 5
        );
        // 카테고리 목록 stubbing
        Category readingCat = createCategory("READING");
        when(categoryRepository.findCategoriesByName(List.of("READING"))).thenReturn(List.of(readingCat));
        List<TodoDifficulty> difficulties = List.of(TodoDifficulty.EASY);

        // fake Slice
        Todo todo = createTodo(20L, "Recommended Todo", member, readingCat, TodoType.RECOMMEND);
        // 난이도도 EASY로 설정
        todo.updateTodo(null, null, TodoDifficulty.EASY, null);
        List<Todo> recommendTodos = List.of(todo);
        Slice<Todo> fakeSlice = new SliceImpl<>(recommendTodos, PageRequest.ofSize(5), false);

        when(todoRepository.findTodosUsingCompositeCursor(
                cursor,
                TodoSearchCond.builder()
                        .type(TodoType.RECOMMEND)
                        .categories(List.of(readingCat))
                        .difficulties(difficulties)
                        .build(),
                PageRequest.ofSize(5)
        )).thenReturn(fakeSlice);

        // 즐겨찾기 할 일 부모 ID
        when(todoRepository.findParentTodoIdsByParentTodoAndMemberAndType(recommendTodos, member, TodoType.SAVE))
                .thenReturn(List.of()); // 예: 자식 없음

        // 실행
        PageResponse<Cursor, List<TodoInfo>> response =
                service.findAllRecommendTodos(new TodoIdentifier(memberId, null, null), cursor, request);

        // 검증
        assertNotNull(response);
        assertFalse(response.hasNext());
        assertNotNull(response.nextCursor());
        List<TodoInfo> infos = response.data();
        assertEquals(1, infos.size());
        assertEquals("Recommended Todo", infos.get(0).title());
    }

    @Test
    @DisplayName("findAllRecommendTodos 실패: 회원 미존재 시 MemberNotFoundException 발생")
    void findAllRecommendTodos_fail_memberNotFound() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());
        Cursor cursor = Cursor.of(1L, TodoDifficulty.NORMAL, 10L);
        RecommendTodoQueryRequest request = new RecommendTodoQueryRequest(
                List.of("CAT"), List.of("EASY"), 5
        );
        assertThrows(MemberNotFoundException.class, () ->
                service.findAllRecommendTodos(new TodoIdentifier(999L, null, null), cursor, request));
    }

    @Test
    @DisplayName("findAllRecommendTodos 실패: 회원 상태가 ONBOARDING, FEEDBACK이면 InvalidMemberStatusException 발생")
    void findAllRecommendTodos_fail_invalidMemberStatus() {
        Long memberId = 1L;
        Member member = createMember(memberId, Status.ONBOARDING);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        RecommendTodoQueryRequest request = new RecommendTodoQueryRequest(List.of("READING"), List.of("EASY"), 5);
        Cursor cursor = Cursor.of(1L, TodoDifficulty.EASY, 10L);

        assertThrows(InvalidMemberStatusException.class, () ->
                service.findAllRecommendTodos(new TodoIdentifier(memberId, null, null), cursor, request));
    }
}