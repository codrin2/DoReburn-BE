import { useMutation, useQueryClient } from '@tanstack/react-query';

import {
  addTodoFromArchived,
  FavoriteTodoResponse,
  RecommendAllTodoResponse,
  RecommendTodo,
} from '@/api/todo';
import { QUERY_KEY } from '@/constants/queryKey';
import { CategoryType, DifficultyType } from '@/types/filter';
import { TodoType } from '@/types/todo';

const useAddTodoFromArchivedMutation = (
  categoryList?: CategoryType[],
  difficultyList?: DifficultyType[],
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      todoType,
      todoId,
      planId,
    }: {
      todoType: TodoType;
      todoId: number;
      planId?: number;
    }) => addTodoFromArchived(todoType, todoId, planId),
    onSuccess: (_, { todoId, planId, todoType }) => {
      // 응답값으로 쿼리 캐싱 갱신 (추천 할 일)
      queryClient.setQueryData<RecommendTodo[]>(
        [QUERY_KEY.recommendLimit, todoType, planId || 0],
        (old) => {
          if (!old) return old;

          return old.map((todo) => {
            if (todo.todoId === todoId) {
              return { ...todo, hasChild: !todo.hasChild };
            }

            return todo;
          });
        },
      );

      // 응답값으로 쿼리 캐싱 갱신 (즐겨찾기)
      queryClient.setQueryData<{ pages: FavoriteTodoResponse[] }>(
        [QUERY_KEY.favorite, todoType, planId || 0],
        (old) => {
          if (!old) return old;

          return {
            ...old,
            pages: old.pages.map((page) => ({
              ...page,
              data: page.data.map((todo) =>
                todo.todoId === todoId ? { ...todo, hasChild: !todo.hasChild } : todo,
              ),
            })),
          };
        },
      );

      // 응답값으로 쿼리 캐싱 갱신 (모든 추천)
      queryClient.setQueryData<{ pages: RecommendAllTodoResponse[] }>(
        [
          QUERY_KEY.recommendAll,
          todoType,
          ...(categoryList || []),
          ...(difficultyList || []),
          planId || 0,
        ],
        (old) => {
          if (!old) return old;

          return {
            ...old,
            pages: old.pages.map((page) => ({
              ...page,
              data: page.data.map((todo) =>
                todo.todoId === todoId ? { ...todo, hasChild: !todo.hasChild } : todo,
              ),
            })),
          };
        },
      );

      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.todoList] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.routeTodoList] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.recommendAll] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.favorite] });
    },
  });
};

export default useAddTodoFromArchivedMutation;
