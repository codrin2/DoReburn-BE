import * as S from './TodoTab.styled';
import TodoEditItem from '../TodoEditItem';
import useRouteTodoQuery from './hooks/useRouteTodoQuery';
import { TODO_TAB_TEXT } from './TodoTab.constants';

import IconButton from '@/components/Button/IconButton';
import Icon from '@/components/Icon';
import { MAX_TODO_ITEM_LENGTH, TODO_TYPE } from '@/constants/config';
import { TODO_TOAST_MESSAGE } from '@/constants/message';
import useAddTodoBottomSheet from '@/hooks/todo/useAddTodoBottomSheet';
import useDeleteTodoMutation from '@/hooks/todo/useDeleteTodoMutation';
import useEditTodoBottomSheet from '@/hooks/todo/useEditTodoBottomSheet';
import useTodoListQuery from '@/hooks/todo/useTodoListQuery';
import useQueryParamsDate from '@/hooks/useQueryParamsDate';
import useToast from '@/hooks/useToast';
import { TodoType } from '@/types/todo';

interface TodoTabProps {
  todoType: TodoType;
  planId?: number;
}

const TodoTab = ({ todoType, planId }: TodoTabProps) => {
  const { dateType } = useQueryParamsDate();

  const { data: currentTodoList } = useTodoListQuery(dateType, Number(planId));
  const { data: routeTodoList } = useRouteTodoQuery(Number(planId));
  const { mutate: deleteTodo } = useDeleteTodoMutation(todoType);

  const todoList = currentTodoList || routeTodoList;

  const { toast } = useToast();
  const openAddTodoBottomSheet = useAddTodoBottomSheet({ todoType, planId });
  const openEditBottomSheet = useEditTodoBottomSheet({ todoType });

  const handleClickAddTodo = () => {
    const isLimitType = todoType === TODO_TYPE.TODAY || todoType === TODO_TYPE.TOMORROW;

    if (isLimitType && currentTodoList && currentTodoList.length >= MAX_TODO_ITEM_LENGTH) {
      toast({ message: TODO_TOAST_MESSAGE.limit(dateType) });

      return;
    }

    openAddTodoBottomSheet();
  };

  const handleDeleteTodo = (todoId: number) => {
    deleteTodo(
      { todoId },
      { onSuccess: () => toast({ message: TODO_TOAST_MESSAGE.delete(todoType) }) },
    );
  };

  if (!todoList) return null;

  return (
    <S.TodoTabLayout>
      <S.SloganWrapper>{TODO_TAB_TEXT[todoType]}</S.SloganWrapper>
      <S.TodoEditList>
        {todoList.map((todo) => (
          <TodoEditItem
            key={todo.todoId}
            todo={todo}
            left={
              <IconButton
                icon={<Icon icon="MinusCircle" cursor="pointer" />}
                onClick={() => handleDeleteTodo(todo.todoId)}
              />
            }
            right={
              <IconButton
                icon={<Icon icon="Edit" cursor="pointer" />}
                onClick={() => openEditBottomSheet(todo)}
              />
            }
          />
        ))}
        <IconButton
          icon={<Icon icon="PlusCircle" cursor="pointer" />}
          text="직접 추가하기"
          isFull={true}
          onClick={handleClickAddTodo}
        />
      </S.TodoEditList>
    </S.TodoTabLayout>
  );
};

export default TodoTab;
