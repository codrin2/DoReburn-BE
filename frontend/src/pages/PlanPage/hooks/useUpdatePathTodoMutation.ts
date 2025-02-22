import { useMutation, useQueryClient } from '@tanstack/react-query';

import { updatePathTodo } from '@/api/todo';
import { QUERY_KEY } from '@/constants/queryKey';

const useUpdatePathTodoMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ todoId, newPathId }: { todoId: number; newPathId: number }) =>
      updatePathTodo({ todoId, newPathId }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.planInfo] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.recommendLimit] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.recommendAll] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.routeTodoList] });
    },
  });
};

export default useUpdatePathTodoMutation;
