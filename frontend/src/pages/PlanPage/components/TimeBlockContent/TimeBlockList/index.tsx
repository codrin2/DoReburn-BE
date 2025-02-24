import { TouchEvent } from 'react';

import TimeBlockItem from './TimeBlockItem';
import * as S from './TimeBlockList.styled';
import { DraggingTodo } from '../PlanContent';

import { PathTodo } from '@/api/plan';
import Icon from '@/components/Icon';
import { colors } from '@/styles/theme';

interface TimeBlockListProps {
  todos: PathTodo[];
  draggingTodo: DraggingTodo | null;
  onTouchStart: (e: TouchEvent<HTMLDivElement>, todo: PathTodo) => void;
  onTouchMove: (e: TouchEvent<HTMLDivElement>) => void;
}

const TimeBlockList = ({ todos, draggingTodo, onTouchStart, onTouchMove }: TimeBlockListProps) => {
  if (todos.length === 0) {
    return (
      <S.EmptyTimeBlock>
        <Icon icon="Fire" width={96} height={96} color={colors.green100} />
        <S.EmptyTimeBlockText>나의 시간, 값진 목표로 채워봐요!</S.EmptyTimeBlockText>
      </S.EmptyTimeBlock>
    );
  }

  return (
    <S.TimeBlockList $isDragging={Boolean(draggingTodo)}>
      {todos.map((todo) => (
        <TimeBlockItem
          key={todo.todoId}
          todo={todo}
          onTouchStart={onTouchStart}
          onTouchMove={onTouchMove}
          draggingTodo={draggingTodo}
        />
      ))}
    </S.TimeBlockList>
  );
};

export default TimeBlockList;
