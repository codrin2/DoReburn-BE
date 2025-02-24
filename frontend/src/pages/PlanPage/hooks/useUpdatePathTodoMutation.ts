import { useMutation, useQueryClient } from '@tanstack/react-query';

import { addTodo, filterTodo, findTodoAndPath } from '../PlanPage.utils';

import { PlanInfoResponse } from '@/api/plan';
import { updatePathTodo } from '@/api/todo';
import { QUERY_KEY } from '@/constants/queryKey';

const useUpdatePathTodoMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ todoId, newPathId }: { todoId: number; newPathId: number }) =>
      updatePathTodo({ todoId, newPathId }),

    onSuccess: async (_, { todoId, newPathId }) => {
      queryClient.setQueryData<PlanInfoResponse['data']>([QUERY_KEY.planInfo], (oldData) => {
        if (!oldData) return oldData;

        const { oldPath, oldTodo } = findTodoAndPath(oldData.paths, todoId);

        if (!oldPath || !oldTodo) return oldData;

        const updatedPaths = filterTodo(oldData.paths, oldPath.pathId, todoId);
        const finalPaths = addTodo(updatedPaths, newPathId, oldTodo);

        return { ...oldData, paths: finalPaths };
      });

      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.favorite] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.recommendLimit] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.recommendAll] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.routeTodoList] });
    },
  });
};

export default useUpdatePathTodoMutation;
