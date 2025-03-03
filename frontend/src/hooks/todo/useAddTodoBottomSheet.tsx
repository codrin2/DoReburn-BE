import useAddTodoMutation from './useAddTodoMutation';

import { TodoCreateParams } from '@/api/todo';
import TodoAddForm from '@/components/TodoAddForm';
import { TODO_TOAST_MESSAGE } from '@/constants/message';
import useOverlay from '@/hooks/useOverlay';
import useToast from '@/hooks/useToast';
import { TodoType } from '@/types/todo';

interface UseAddTodoBottomSheetProps {
  todoType: TodoType;
  planId?: number;
}

const useAddTodoBottomSheet = ({ todoType, planId }: UseAddTodoBottomSheetProps) => {
  const overlay = useOverlay();
  const { mutate: addTodo, isPending } = useAddTodoMutation();
  const { toast } = useToast();

  const handleAddTodo = (todo: TodoCreateParams) => {
    addTodo(
      { todoType, todo, planId },
      {
        onSuccess: () => {
          toast({ message: TODO_TOAST_MESSAGE.add(todoType) });
          overlay.close();
        },
      },
    );
  };

  const openAddTodoBottomSheet = () => {
    overlay.open(() => <TodoAddForm handleAddTodo={handleAddTodo} isLoading={isPending} />, {
      title: '할 일 추가하기',
    });
  };

  return openAddTodoBottomSheet;
};

export default useAddTodoBottomSheet;
