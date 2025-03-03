import * as S from './TodoEditItem.styled';

import { CATEGORY_MAPPER } from '@/constants/config';
import { Todo } from '@/types/todo';

const DIFFICULTY_MAPPER = {
  EASY: '쉬움',
  NORMAL: '보통',
  HARD: '어려움',
} as const;

interface TodoEditItemProps {
  todo: Todo;
  left?: React.ReactNode;
  right?: React.ReactNode;
  disabled?: boolean;
}

const TodoEditItem = ({ todo, left, right, disabled }: TodoEditItemProps) => {
  return (
    <S.TodoEditItem>
      {left}
      <S.TodoTextWrapper>
        <S.TodoTitle $disabled={disabled}>{todo.title}</S.TodoTitle>
        <S.TodoBadgeWrapper>
          <S.TodoBadge
            $category={todo.category}
          >{`#${CATEGORY_MAPPER[todo.category]}`}</S.TodoBadge>
          <S.TodoBadge>{`#${DIFFICULTY_MAPPER[todo.difficulty]}`}</S.TodoBadge>
        </S.TodoBadgeWrapper>
      </S.TodoTextWrapper>
      {right}
    </S.TodoEditItem>
  );
};

export default TodoEditItem;
