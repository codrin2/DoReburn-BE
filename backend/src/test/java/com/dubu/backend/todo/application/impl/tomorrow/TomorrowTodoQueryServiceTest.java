package com.dubu.backend.todo.application.impl.tomorrow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.domain.repository.MemberCategoryRepository;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.todo.domain.Schedule;
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
import com.dubu.backend.todo.infra.repository.ScheduleRepository;
import com.dubu.backend.todo.infra.repository.TodoRepository;
import com.dubu.backend.todo.application.support.TodoRandomSelector;
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
class TomorrowTodoQueryServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private MemberCategoryRepository memberCategoryRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TodoRepository todoRepository;
    @Mock private ScheduleRepository scheduleRepository;
    @Mock private TodoRandomSelector todoRandomSelector;

    @InjectMocks
    private TomorrowTodoQueryService service;

    // 헬퍼 메서드: Member 생성 (내일 쿼리는 회원 상태가 정지(STOP)여야 함)
    private Member createMember(Long memberId, Status status) {
        return Member.builder()
                .id(memberId)
                .nickname("testUser")
                .email("test@example.com")
                .status(status)
                .build();
    }

    // 헬퍼 메서드: Schedule 생성
    private Schedule createSchedule(LocalDate date, Member member) {
        return Schedule.of(date, member);
    }

    // 헬퍼 메서드: Category 생성
    private Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .build();
    }

    // 헬퍼 메서드: Todo 생성 (Todo의 category는 null이 아니도록 설정)
    private Todo createTodo(Long todoId, String title, Member member, Category category, TodoType type) {
        return Todo.builder()
                .id(todoId)
                .title(title)
                .category(category)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("Memo " + todoId)
                .member(member)
                .type(type)
                .build();
    }

    // =========================================================
    // findTargetTodos 테스트 (내일 스케줄 관련)
    // =========================================================
    @Test
    @DisplayName("findTargetTodos 성공: 정지 상태 회원, 내일 스케줄 존재 시 할 일 목록 반환")
    void findTargetTodos_success_existingSchedule() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        Member member = createMember(memberId, Status.STOP);
        // 내일 스케줄 생성 (LocalDate.now().plusDays(1))
        Schedule tomorrowSchedule = createSchedule(LocalDate.now().plusDays(1), member);
        Category category = createCategory("READING");

        // 내일 스케줄에 연결된 Todo 2건
        Todo todo1 = Todo.builder()
                .id(10L)
                .title("Tomorrow Todo 1")
                .category(category)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("Memo 1")
                .member(member)
                .type(TodoType.RECOMMEND)
                .build();
        Todo todo2 = Todo.builder()
                .id(11L)
                .title("Tomorrow Todo 2")
                .category(category)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("Memo 2")
                .member(member)
                .type(TodoType.RECOMMEND)
                .build();
        List<Todo> todos = List.of(todo1, todo2);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1))).thenReturn(Optional.of(tomorrowSchedule));
        when(todoRepository.findTodosWithCategoryBySchedule(tomorrowSchedule)).thenReturn(todos);

        List<TodoInfo> result = service.findTargetTodos(identifier);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tomorrow Todo 1", result.get(0).title());
        assertEquals("Tomorrow Todo 2", result.get(1).title());
    }

    // =========================================================
    // findSaveTodos 테스트 (내일 스케줄 기반 페이징)
    // =========================================================
    @Test
    @DisplayName("findSaveTodos 성공: 정지 상태 회원의 내일 저장된 할 일 목록 페이지 반환")
    void findSaveTodos_success() {
        // Given
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        SaveTodoQueryRequest request = new SaveTodoQueryRequest(5);

        Member member = createMember(memberId, Status.STOP);
        Schedule tomorrowSchedule = createSchedule(LocalDate.now().plusDays(1), member);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1)))
                .thenReturn(Optional.of(tomorrowSchedule));

        // "CAT" 카테고리, 난이도 NORMAL을 가지는 2개의 Todo 생성
        Category category = createCategory("CAT");
        Todo todo1 = Todo.builder()
                .id(20L)
                .title("Save Todo 1")
                .category(category)
                .member(member)
                .type(TodoType.SAVE)
                .difficulty(TodoDifficulty.NORMAL)  // 반드시 설정
                .build();

        Todo todo2 = Todo.builder()
                .id(21L)
                .title("Save Todo 2")
                .category(category)
                .member(member)
                .type(TodoType.SAVE)
                .difficulty(TodoDifficulty.NORMAL)  // 반드시 설정
                .build();

        List<Todo> todos = List.of(todo1, todo2);
        // 페이징 처리를 위한 SliceImpl
        Slice<Todo> fakeSlice = new SliceImpl<>(todos, PageRequest.ofSize(request.size()), false);

        // stubbing: 커서 기반 조회
        when(todoRepository.findTodosUsingSingleCursor(null,
                TodoSearchCond.builder().member(member).type(TodoType.SAVE).build(),
                PageRequest.ofSize(request.size())))
                .thenReturn(fakeSlice);

        // stubbing: 내일 스케줄에 등록된 부모 Todo ID 목록
        when(todoRepository.findParentTodoIdsByScheduleAndParentTodoNotNull(tomorrowSchedule))
                .thenReturn(List.of(30L));

        // When
        PageResponse<Long, List<TodoInfo>> response = service.findSaveTodos(identifier, null, request);

        // Then
        assertNotNull(response);
        assertFalse(response.hasNext());
        assertNotNull(response.data());
        assertEquals(2, response.data().size());

        // 각 TodoInfo의 필드 검증
        assertEquals("Save Todo 1", response.data().get(0).title());
        assertEquals("Save Todo 2", response.data().get(1).title());
    }
    // =========================================================
    // findPersonalizedRecommendTodos 테스트 (내일 추천 할 일)
    // =========================================================
    @Test
    @DisplayName("findPersonalizedRecommendTodos 성공: 정지 상태 회원의 내일 개인 맞춤 추천 할 일 반환")
    void findPersonalizedRecommendTodos_success() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        Member member = createMember(memberId, Status.STOP);
        Schedule tomorrowSchedule = createSchedule(LocalDate.now().plusDays(1), member);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1))).thenReturn(Optional.of(tomorrowSchedule));

        // 회원의 카테고리 정보로 Todo ID 목록 조회
        when(memberCategoryRepository.findCategoryIdsByMember(member)).thenReturn(List.of(1L));
        when(todoRepository.findTodosWithCategoryByCategoryIdsAndType(List.of(1L), TodoType.RECOMMEND))
                .thenReturn(List.of(100L, 101L));
        when(todoRandomSelector.selectTodos(5, List.of(100L, 101L))).thenReturn(List.of(100L));
        Category category = createCategory("READING");
        Todo recommendedTodo = Todo.builder()
                .id(100L)
                .title("Personalized Todo")
                .category(category)
                .difficulty(TodoDifficulty.EASY)
                .memo("Memo")
                .member(member)
                .type(TodoType.RECOMMEND)
                .build();
        when(todoRepository.findAllById(List.of(100L))).thenReturn(List.of(recommendedTodo));

        when(todoRepository.findParentTodoIdsByScheduleAndParentTodoNotNull(tomorrowSchedule))
                .thenReturn(List.of(50L));
        when(todoRepository.findParentTodoIdsByIdsAndTypeAndParentTodoNotNull(List.of(50L), TodoType.SAVE))
                .thenReturn(List.of(60L));

        List<TodoInfo> result = service.findPersonalizedRecommendTodos(identifier);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Personalized Todo", result.get(0).title());
    }

    // =========================================================
    // findAllRecommendTodos 테스트 (내일 추천 할 일 페이징)
    // =========================================================
    @Test
    @DisplayName("findAllRecommendTodos 성공: 정지 상태 회원의 내일 추천 할 일 페이지 반환")
    void findAllRecommendTodos_success() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        Cursor cursor = Cursor.of(1L, TodoDifficulty.EASY, 10L);
        RecommendTodoQueryRequest request = new RecommendTodoQueryRequest(
                List.of("READING"), List.of("EASY"), 5);

        Member member = createMember(memberId, Status.STOP);
        Schedule tomorrowSchedule = createSchedule(LocalDate.now().plusDays(1), member);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now().plusDays(1))).thenReturn(Optional.of(tomorrowSchedule));

        // Category stubbing
        List<Category> categories = List.of(createCategory("READING"));
        when(categoryRepository.findCategoriesByName(request.category())).thenReturn(categories);
        List<TodoDifficulty> difficulties = List.of(TodoDifficulty.EASY);

        // Fake Slice<Todo> 생성: 내일 추천 할 일 1건, 다음 페이지 없음
        Category cat = createCategory("READING");
        Todo todo = Todo.builder()
                .id(20L)
                .title("Recommend Todo")
                .category(cat)
                .difficulty(TodoDifficulty.EASY)
                .memo("Memo")
                .member(member)
                .type(TodoType.RECOMMEND)
                .build();
        List<Todo> todos = List.of(todo);
        Slice<Todo> fakeSlice = new SliceImpl<>(todos, PageRequest.ofSize(request.size()), false);
        when(todoRepository.findTodosUsingCompositeCursor(cursor,
                TodoSearchCond.builder()
                        .type(TodoType.RECOMMEND)
                        .categories(categories)
                        .difficulties(difficulties)
                        .build(),
                PageRequest.ofSize(request.size()))).thenReturn(fakeSlice);

        when(todoRepository.findParentTodoIdsByScheduleAndParentTodoNotNull(tomorrowSchedule))
                .thenReturn(List.of(30L));
        when(todoRepository.findParentTodoIdsByIdsAndTypeAndParentTodoNotNull(List.of(30L), TodoType.SAVE))
                .thenReturn(List.of(40L));

        PageResponse<Cursor, List<TodoInfo>> response = service.findAllRecommendTodos(identifier, cursor, request);
        assertNotNull(response);
        assertFalse(response.hasNext());
        assertNotNull(response.nextCursor());
        List<TodoInfo> infos = response.data();
        assertEquals(1, infos.size());
        assertEquals("Recommend Todo", infos.get(0).title());
    }
}