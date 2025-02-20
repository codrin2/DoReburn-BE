import useEditTodoMutation from './useEditTodoMutation';
import TodoEditForm from '../components/TodoEditForm';

import useOverlay from '@/hooks/useOverlay';
import { Todo, TodoType } from '@/types/todo';

interface UseAddBottomSheetProps {
  todoType: TodoType;
  planId?: number;
}

const useEditBottomSheet = ({ todoType, planId }: UseAddBottomSheetProps) => {
  const { mutate: editTodo } = useEditTodoMutation(todoType);
  const overlay = useOverlay();

  const handleEditTodo = (todo: Todo) => {
    editTodo(
      { todo, planId },
      {
        onSuccess: () => {
          overlay.close();
        },
      },
    );
  };

  const openEditTodoBottomSheet = (todo: Todo) => {
    overlay.open(() => <TodoEditForm handleEditTodo={handleEditTodo} todo={todo} />, {
      title: '할 일 수정하기',
    });
  };

  return openEditTodoBottomSheet;
};

export default useEditBottomSheet;
