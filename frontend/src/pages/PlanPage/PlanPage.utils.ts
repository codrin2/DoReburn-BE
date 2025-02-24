import { Path, PathTodo } from '@/api/plan';
import { formatDateHeader } from '@/utils/time';

export const formatStartTime = (dateString: string) => {
  const date = new Date(dateString);

  const formattedDate = formatDateHeader(date);

  const time = date.toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
  });

  return { date: formattedDate, time };
};

export const findTodoAndPath = (paths: Path[], todoId: number) => {
  for (const path of paths) {
    const foundTodo = path.todos.find((todo) => todo.todoId === todoId);

    if (foundTodo) {
      return { oldPath: path, oldTodo: foundTodo };
    }
  }

  return { oldPath: null, oldTodo: null };
};

export const filterTodo = (paths: Path[], oldPathId: number, todoId: number) => {
  return paths.map((path) =>
    path.pathId === oldPathId
      ? { ...path, todos: path.todos.filter((todo) => todo.todoId !== todoId) }
      : path,
  );
};

export const addTodo = (paths: Path[], newPathId: number, todo: PathTodo) => {
  return paths.map((path) =>
    path.pathId === newPathId ? { ...path, todos: [...path.todos, todo] } : path,
  );
};
