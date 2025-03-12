package com.dubu.backend.todo.application.impl.today;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dubu.backend.member.domain.Member;
import com.dubu.backend.member.domain.enums.Status;
import com.dubu.backend.member.domain.repository.MemberRepository;
import com.dubu.backend.member.domain.repository.MemberCategoryRepository;
import com.dubu.backend.todo.domain.Category;
import com.dubu.backend.todo.domain.Schedule;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TodayTodoQueryServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private MemberCategoryRepository memberCategoryRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TodoRepository todoRepository;
    @Mock private ScheduleRepository scheduleRepository;
    @Mock private TodoRandomSelector todoRandomSelector;

    @InjectMocks
    private TodayTodoQueryService service;

    // 헬퍼 메서드
    private Member createMember(Long memberId, Status status) {
        return Member.builder()
                .id(memberId)
                .nickname("testUser")
                .email("test@example.com")
                // 필요한 경우 oauthProvider, oauthProviderId, role 설정
                .status(status)
                .build();
    }

    private Schedule createSchedule(LocalDate date, Member member) {
        return Schedule.of(date, member);
    }

    private Category createCategory(String name) {
        return Category.builder().name(name).build();
    }

    private Todo createTodo(Long todoId, String title) {
        // Category를 반드시 생성해서 할당합니다.
        Category category = createCategory("Default Category");
        return Todo.builder()
                .id(todoId)
                .title(title)
                .category(category)  // null이 아니도록 설정
                .difficulty(TodoDifficulty.NORMAL)
                .memo("기본 메모")
                .member(null) // 테스트에 필요한 경우 적절히 설정
                .type(TodoType.RECOMMEND) // 테스트 상황에 맞게 타입 지정
                .build();
    }

    // =========================================================
    // findTargetTodos 테스트
    // =========================================================
    @Test
    @DisplayName("findTargetTodos 성공: 정지 상태 회원, 오늘 스케줄 존재 시 할 일 목록 반환")
    void findTargetTodos_success_existingSchedule() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        Member member = createMember(memberId, Status.STOP);
        Schedule todaySchedule = createSchedule(LocalDate.now(), member);
        Category category = createCategory("READING");

        // 오늘 스케줄에 연결된 Todo 2건 생성 (Category가 null이 아니도록 설정)
        Todo todo1 = Todo.builder()
                .id(10L)
                .title("Todo 1")
                .category(category)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("Memo 1")
                .member(member)
                .type(TodoType.RECOMMEND)
                .build();
        Todo todo2 = Todo.builder()
                .id(11L)
                .title("Todo 2")
                .category(category)
                .difficulty(TodoDifficulty.NORMAL)
                .memo("Memo 2")
                .member(member)
                .type(TodoType.RECOMMEND)
                .build();
        List<Todo> todos = List.of(todo1, todo2);

        // stubbing
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(todaySchedule));
        // 회원의 카테고리 ID 목록 (예시로 1, 2)
        when(memberCategoryRepository.findCategoryIdsByMember(member)).thenReturn(List.of(1L, 2L));
        // 추천할 Todo ID 목록
        when(todoRepository.findTodosWithCategoryByCategoryIdsAndType(List.of(1L, 2L), TodoType.RECOMMEND))
                .thenReturn(List.of(100L, 101L));
        // TodoRandomSelector가 3건 중 1건 선택한다고 가정
        when(todoRandomSelector.selectTodos(3, List.of(100L, 101L))).thenReturn(List.of(100L));
        // 추천 Todo 생성 (Category가 null이 아니도록 설정)
        Todo recommendedTodo = Todo.builder()
                .id(100L)
                .title("Recommended Todo")
                .category(category)
                .difficulty(TodoDifficulty.EASY)
                .memo("추천 메모")
                .member(member)
                .type(TodoType.RECOMMEND)
                .build();
        when(todoRepository.findTodosWithCategoryByIds(List.of(100L)))
                .thenReturn(List.of(recommendedTodo));
        // 오늘 스케줄에 연결된 Todo 목록 반환
        when(todoRepository.findTodosWithCategoryBySchedule(todaySchedule)).thenReturn(todos);

        List<TodoInfo> result = service.findTargetTodos(identifier);
        assertNotNull(result);
        assertEquals(2, result.size());
        // 각 TodoInfo의 제목이 Todo1, Todo2인지 검증
        assertEquals("Todo 1", result.get(0).title());
        assertEquals("Todo 2", result.get(1).title());
    }

    // =========================================================
    // findSaveTodos 테스트
    // =========================================================
    // 간단한 Slice를 생성하는 예시
    @Test
    @DisplayName("findSaveTodos 성공: 정지 상태 회원의 저장된 할 일 목록 페이지 반환")
    void findSaveTodos_success() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        SaveTodoQueryRequest request = new SaveTodoQueryRequest(5);
        Member member = createMember(memberId, Status.STOP);
        Schedule todaySchedule = createSchedule(LocalDate.now(), member);

        // stubbing: 회원, 스케줄 조회
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(todaySchedule));

        // 가상의 Slice<Todo> 생성
        List<Todo> todos = new ArrayList<>();
        todos.add(createTodo(20L, "Save Todo 1"));
        todos.add(createTodo(21L, "Save Todo 2"));
        Slice<Todo> fakeSlice = new SliceImpl<>(todos, PageRequest.ofSize(5), false);
        when(todoRepository.findTodosUsingSingleCursor(null,
                TodoSearchCond.builder().member(member).type(TodoType.SAVE).build(), PageRequest.ofSize(request.size())))
                .thenReturn(fakeSlice);

        // 부모 할 일 ID 목록
        when(todoRepository.findParentTodoIdsByScheduleAndParentTodoNotNull(todaySchedule))
                .thenReturn(List.of(30L));

        // 반환할 PageResponse 생성
        PageResponse<Long, List<TodoInfo>> response = service.findSaveTodos(identifier, null, request);
        assertNotNull(response);
        assertFalse(response.hasNext());
        assertNotNull(response.data());
        assertEquals(2, response.data().size());
    }

    // =========================================================
    // findPersonalizedRecommendTodos 테스트
    // =========================================================
    @Test
    @DisplayName("findPersonalizedRecommendTodos 성공: 정지 상태 회원의 개인 맞춤 추천 할 일 반환")
    void findPersonalizedRecommendTodos_success() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        Member member = createMember(memberId, Status.STOP);
        Schedule todaySchedule = createSchedule(LocalDate.now(), member);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(todaySchedule));
        when(memberCategoryRepository.findCategoryIdsByMember(member)).thenReturn(List.of(1L));
        when(todoRepository.findTodosWithCategoryByCategoryIdsAndType(List.of(1L), TodoType.RECOMMEND))
                .thenReturn(List.of(100L, 101L));
        when(todoRandomSelector.selectTodos(5, List.of(100L, 101L))).thenReturn(List.of(100L));
        when(todoRepository.findAllById(List.of(100L)))
                .thenReturn(List.of(createTodo(100L, "Personalized Todo")));
        when(todoRepository.findParentTodoIdsByScheduleAndParentTodoNotNull(todaySchedule))
                .thenReturn(List.of(50L));
        when(todoRepository.findParentTodoIdsByIdsAndTypeAndParentTodoNotNull(List.of(50L), TodoType.SAVE))
                .thenReturn(List.of(60L));

        List<TodoInfo> result = service.findPersonalizedRecommendTodos(identifier);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // =========================================================
    // findAllRecommendTodos 테스트
    // =========================================================
    @Test
    @DisplayName("findAllRecommendTodos 성공: 정지 상태 회원의 추천 할 일 페이지 반환")
    void findAllRecommendTodos_success() {
        Long memberId = 1L;
        TodoIdentifier identifier = new TodoIdentifier(memberId, null, null);
        // 예시 Cursor (구조에 맞게 생성)
        Cursor cursor = Cursor.of(1L, TodoDifficulty.EASY, 10L);
        RecommendTodoQueryRequest request = new RecommendTodoQueryRequest(
                List.of("READING"), // 카테고리 필터
                List.of("EASY"),    // 난이도 필터
                5                   // 페이지 크기
        );

        Member member = createMember(memberId, Status.STOP);
        Schedule todaySchedule = createSchedule(LocalDate.now(), member);

        // stubbing: 회원 및 스케줄 조회
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(scheduleRepository.findLatestSchedule(member, LocalDate.now())).thenReturn(Optional.of(todaySchedule));

        // 요청에 포함된 카테고리 문자열을 기반으로 Category 목록 반환
        List<Category> categories = List.of(createCategory("READING"));
        when(categoryRepository.findCategoriesByName(request.category())).thenReturn(categories);

        // 요청 난이도 "EASY"를 TodoDifficulty 리스트로 매핑
        List<TodoDifficulty> difficulties = List.of(TodoDifficulty.EASY);

        // 가상의 Slice<Todo> 생성: 할 일 1건, 다음 페이지 없음
        Todo todo = Todo.builder()
                .id(20L)
                .title("Recommend Todo")
                .category(createCategory("READING")) // null이 아님
                .difficulty(TodoDifficulty.EASY)
                .memo("추천 메모")
                .member(member)
                .type(TodoType.RECOMMEND)
                .build();
        List<Todo> todos = List.of(todo);
        Slice<Todo> fakeSlice = new SliceImpl<>(todos, PageRequest.ofSize(request.size()), false);

        when(todoRepository.findTodosUsingCompositeCursor(
                cursor,
                TodoSearchCond.builder()
                        .type(TodoType.RECOMMEND)
                        .categories(categories)
                        .difficulties(difficulties)
                        .build(),
                PageRequest.ofSize(request.size())))
                .thenReturn(fakeSlice);

        // stubbing: 오늘 스케줄의 부모 할 일 ID 목록 (예시)
        when(todoRepository.findParentTodoIdsByScheduleAndParentTodoNotNull(todaySchedule))
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