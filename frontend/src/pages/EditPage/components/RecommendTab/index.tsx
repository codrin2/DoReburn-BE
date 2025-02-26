import * as S from './RecommendTab.styled';
import useAddTodoFromArchivedMutation from '../../hooks/useAddTodoFromArchivedMutation';
import TodoEditItem from '../TodoEditItem';

import IconButton from '@/components/Button/IconButton';
import Icon from '@/components/Icon';
import { TODO_TOAST_MESSAGE } from '@/constants/message';
import useToast from '@/hooks/useToast';
import useRecommendTodoListQuery from '@/pages/EditPage/hooks/useRecommendListQuery';
import { TodoType } from '@/types/todo';

interface RecommendTabProps {
  todoType: TodoType;
  planId?: number;
}

const RecommendTab = ({ todoType, planId }: RecommendTabProps) => {
  const { data: recommendTodoList } = useRecommendTodoListQuery(todoType, Number(planId));
  const { mutate: addTodoFromArchived } = useAddTodoFromArchivedMutation();
  const { toast } = useToast();

  const routeURL = planId ? `/recommend/${planId}` : '/recommend';
  const finalURL = `${routeURL}?dateType=${todoType}`;

  const handleAddTodoFromRecommend = (todoId: number) => {
    addTodoFromArchived(
      { todoType, todoId, planId: Number(planId) },
      { onSuccess: () => toast({ message: TODO_TOAST_MESSAGE.add(todoType) }) },
    );
  };

  if (!recommendTodoList) return null;

  return (
    <>
      <S.RecommendTabList>
        {recommendTodoList.map((todo) => (
          <TodoEditItem
            key={todo.todoId}
            todo={todo}
            disabled={todo.hasChild}
            left={
              <IconButton
                icon={
                  todo.hasChild ? (
                    <Icon icon="CheckCircle" cursor="pointer" />
                  ) : (
                    <Icon icon="PlusCircle" cursor="pointer" />
                  )
                }
                onClick={() => handleAddTodoFromRecommend(todo.todoId)}
                disabled={todo.hasChild}
              />
            }
          />
        ))}
      </S.RecommendTabList>
      <S.WatchMoreLinkWrapper>
        <S.WatchMoreLink to={finalURL}>더보기</S.WatchMoreLink>
      </S.WatchMoreLinkWrapper>
    </>
  );
};

export default RecommendTab;
