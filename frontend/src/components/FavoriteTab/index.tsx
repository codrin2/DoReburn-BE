import * as S from './FavoriteTab.styled';
import TodoEditItem from '../TodoEditItem';
import useFavoriteTodoListQuery from './hooks/useFavoriteListQuery';

import IconButton from '@/components/Button/IconButton';
import Icon from '@/components/Icon';
import IntersectionObserverScroll from '@/components/IntersectionObserverScroll';
import { TODO_TYPE } from '@/constants/config';
import { TODO_TOAST_MESSAGE } from '@/constants/message';
import useAddTodoBottomSheet from '@/hooks/todo/useAddTodoBottomSheet';
import useAddTodoFromArchivedMutation from '@/hooks/todo/useAddTodoFromArchivedMutation';
import useDeleteTodoMutation from '@/hooks/todo/useDeleteTodoMutation';
import useEditTodoBottomSheet from '@/hooks/todo/useEditTodoBottomSheet';
import useToast from '@/hooks/useToast';
import { TodoType } from '@/types/todo';

interface FavoriteTabProps {
  todoType: TodoType;
  planId?: number;
}

const FavoriteTab = ({ todoType, planId }: FavoriteTabProps) => {
  const {
    data: favoriteTodoList,
    hasNextPage,
    fetchNextPage,
    isFetching,
  } = useFavoriteTodoListQuery(todoType, Number(planId));

  const { mutate: addTodoFromArchived } = useAddTodoFromArchivedMutation();
  const { mutate: deleteTodo } = useDeleteTodoMutation(todoType);

  const { toast } = useToast();
  const openAddBottomSheet = useAddTodoBottomSheet({ todoType, planId });
  const openEditBottomSheet = useEditTodoBottomSheet({ todoType });

  const isFavoritePage = todoType === TODO_TYPE.SAVE;

  const handleAddTodoFromFavorite = (todoId: number) => {
    addTodoFromArchived(
      { todoType, todoId, planId: Number(planId) },
      {
        onSuccess: () => {
          toast({ message: TODO_TOAST_MESSAGE.add(todoType) });
        },
      },
    );
  };

  const handleDeleteTodo = (todoId: number) => {
    deleteTodo(
      { todoId },
      { onSuccess: () => toast({ message: TODO_TOAST_MESSAGE.deleteFavorite }) },
    );
  };

  if (!favoriteTodoList) return null;

  return (
    <>
      <IntersectionObserverScroll hasNextPage={hasNextPage} onReachBottom={fetchNextPage}>
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

          {isFetching && <S.LoadingBlock />}
        </S.FavoriteTabLayout>
      </IntersectionObserverScroll>
    </>
  );
};

export default FavoriteTab;
