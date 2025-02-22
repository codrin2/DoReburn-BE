import { useMutation, useQueryClient } from '@tanstack/react-query';

import { Path, PathTodo, PlanInfoResponse } from '@/api/plan';
import { updatePathTodo } from '@/api/todo';
import { QUERY_KEY } from '@/constants/queryKey';

const findTodoAndPath = (paths: Path[], todoId: number) => {
  for (const path of paths) {
    const foundTodo = path.todos.find((todo) => todo.todoId === todoId);

    if (foundTodo) {
      return { oldPath: path, oldTodo: foundTodo };
    }
  }

  return { oldPath: null, oldTodo: null };
};

const filterTodo = (paths: Path[], oldPathId: number, todoId: number) => {
  return paths.map((path) =>
    path.pathId === oldPathId
      ? { ...path, todos: path.todos.filter((todo) => todo.todoId !== todoId) }
      : path,
  );
};

const addTodo = (paths: Path[], newPathId: number, todo: PathTodo) => {
  return paths.map((path) =>
    path.pathId === newPathId ? { ...path, todos: [...path.todos, todo] } : path,
  );
};

const useUpdatePathTodoMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ todoId, newPathId }: { todoId: number; newPathId: number }) =>
      updatePathTodo({ todoId, newPathId }),
    onSuccess: (_, { todoId, newPathId }) => {
      queryClient.setQueryData<PlanInfoResponse['data']>([QUERY_KEY.planInfo], (oldData) => {
        if (!oldData) return oldData;

        const { oldPath, oldTodo } = findTodoAndPath(oldData.paths, todoId);

        if (!oldPath || !oldTodo) return oldData;

        const updatedPaths = filterTodo(oldData.paths, oldPath.pathId, todoId);
        const finalPaths = addTodo(updatedPaths, newPathId, oldTodo);

        return { ...oldData, paths: finalPaths };
      });

      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.recommendLimit] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.recommendAll] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.routeTodoList] });
    },
  });
};

export default useUpdatePathTodoMutation;
