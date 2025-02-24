import { useMutation, useQueryClient } from '@tanstack/react-query';

import { editTodo } from '@/api/todo';
import { QUERY_KEY } from '@/constants/queryKey';
import { Todo, TodoType } from '@/types/todo';

const useEditTodoMutation = (todoType: TodoType) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ todo, planId }: { todo: Todo; planId?: number }) => editTodo(todo, todoType),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.routeTodoList, todoType] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.todoList, todoType] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.favorite, todoType] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.recommendLimit, todoType] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.recommendAll, todoType] });
    },
  });
};

export default useEditTodoMutation;
