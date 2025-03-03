import { useMutation, useQueryClient } from '@tanstack/react-query';

import { deleteTodo } from '@/api/todo';
import { QUERY_KEY } from '@/constants/queryKey';
import { TodoType } from '@/types/todo';

const useDeleteTodoMutation = (todoType: TodoType) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ todoId }: { todoId: number }) => deleteTodo(todoId, todoType),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.todoList] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.routeTodoList] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.recommendAll] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.favorite] });
    },
  });
};

export default useDeleteTodoMutation;
