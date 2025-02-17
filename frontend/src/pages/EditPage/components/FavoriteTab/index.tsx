import * as S from './FavoriteTab.styled';
import { useAddTodoBottomSheet } from '../../hooks/useAddTodoBottomSheet';
import useAddTodoFromArchivedMutation from '../../hooks/useAddTodoFromArchivedMutation';
import useDeleteTodoMutation from '../../hooks/useDeleteTodoMutation';
import useEditTodoBottomSheet from '../../hooks/useEditTodoBottomSheet';
import useFavoriteTodoListQuery from '../../hooks/useFavoriteListQuery';
import TodoEditItem from '../TodoEditItem';

import BottomSheet from '@/components/BottomSheet';
import IconButton from '@/components/Button/IconButton';
import Icon from '@/components/Icon';
import { MAX_TODO_ITEM_LENGTH, TODO_TYPE } from '@/constants/config';
import { TODO_TOAST_MESSAGE } from '@/constants/message';
import useQueryParamsDate from '@/hooks/useQueryParamsDate';
import useToast from '@/hooks/useToast';
import useTodoListQuery from '@/hooks/useTodoListQuery';
import { TodoType } from '@/types/todo';

interface FavoriteTabProps {
  todoType: TodoType;
  planId?: number;
}

const FavoriteTab = ({ todoType, planId }: FavoriteTabProps) => {
  const { dateType } = useQueryParamsDate();

  const { data: todoList } = useTodoListQuery(dateType, Number(planId));
  const { data: favoriteTodoList } = useFavoriteTodoListQuery(todoType, Number(planId));
  const { mutate: addTodoFromArchived } = useAddTodoFromArchivedMutation();
  const { mutate: deleteTodo } = useDeleteTodoMutation(todoType);

  const { toast } = useToast();
  const {
    isOpen: isAddOpen,
    open: openAddBottomSheet,
    close: closeAddBottomSheet,
    content: addContent,
    title: addTodoForm,
  } = useAddTodoBottomSheet(todoType, planId);

  const {
    isOpen: isEditOpen,
    open: openEditBottomSheet,
    close: closeEditBottomSheet,
    content: editTodoForm,
    title: editTitle,
  } = useEditTodoBottomSheet(todoType, planId);

  const isFavoritePage = todoType === TODO_TYPE.SAVE;

  const handleAddTodoFromFavorite = (todoId: number) => {
    const isLimitType = todoType === TODO_TYPE.TODAY || todoType === TODO_TYPE.TOMORROW;

    if (isLimitType && todoList && todoList.length >= MAX_TODO_ITEM_LENGTH) {
      toast({ message: TODO_TOAST_MESSAGE.limit });

      return;
    }

    addTodoFromArchived(
      { todoType, todoId, planId: Number(planId) },
      {
        onSuccess: () => {
          toast({ message: TODO_TOAST_MESSAGE.add });
        },
      },
    );
  };

  const handleDeleteTodo = (todoId: number) => {
    deleteTodo(
      { todoId, planId },
      { onSuccess: () => toast({ message: TODO_TOAST_MESSAGE.deleteFavorite }) },
    );
  };

  if (!favoriteTodoList) return null;

  return (
    <>
      <S.FavoriteTabLayout>
        {favoriteTodoList.pages.map((page) =>
          page.data.map((todo) => (
            <TodoEditItem
              key={todo.todoId}
              todo={todo}
              disabled={todo.hasChild}
              left={
                isFavoritePage ? (
                  <IconButton
                    icon={<Icon icon="MinusCircle" cursor="pointer" />}
                    onClick={() => handleDeleteTodo(todo.todoId)}
                    disabled={todo.hasChild}
                  />
                ) : (
                  <IconButton
                    icon={
                      todo.hasChild ? (
                        <Icon icon="CheckCircle" cursor="pointer" />
                      ) : (
                        <Icon icon="PlusCircle" cursor="pointer" />
                      )
                    }
                    onClick={() => handleAddTodoFromFavorite(todo.todoId)}
                    disabled={todo.hasChild}
                  />
                )
              }
              right={
                isFavoritePage && (
                  <IconButton
                    icon={<Icon icon="Edit" cursor="pointer" />}
                    onClick={() => openEditBottomSheet(todo)}
                  />
                )
              }
            />
          )),
        )}

        {isFavoritePage && (
          <IconButton
            icon={<Icon icon="PlusCircle" cursor="pointer" />}
            text="직접 추가하기"
            isFull={true}
            onClick={openAddBottomSheet}
          />
        )}
      </S.FavoriteTabLayout>
      <BottomSheet
        isOpen={isAddOpen}
        title={addTodoForm}
        content={addContent}
        onClose={closeAddBottomSheet}
      />
      <BottomSheet
        isOpen={isEditOpen}
        title={editTitle}
        content={editTodoForm}
        onClose={closeEditBottomSheet}
      />
    </>
  );
};

export default FavoriteTab;
