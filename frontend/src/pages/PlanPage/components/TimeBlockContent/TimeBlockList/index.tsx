import { LegacyRef, TouchEvent } from 'react';

import TimeBlockItem from './TimeBlockItem';
import * as S from './TimeBlockList.styled';
import { DraggingTodo } from '../PlanContent';

import { PathTodo } from '@/api/plan';
import Icon from '@/components/Icon';
import { colors } from '@/styles/theme';

interface TimeBlockListProps {
  pathId: number;
  todos: PathTodo[];
  draggingTodo: DraggingTodo | null;
  onTouchStart: (e: TouchEvent<HTMLDivElement>, todo: PathTodo) => void;
  onTouchEnd: (e: TouchEvent<HTMLDivElement>) => void;
  onTouchMove: (e: TouchEvent<HTMLDivElement>) => void;
  itemRef?: LegacyRef<HTMLDivElement>;
}

const TimeBlockList = ({
  pathId,
  todos,
  draggingTodo,
  onTouchStart,
  onTouchEnd,
  onTouchMove,
  itemRef,
}: TimeBlockListProps) => {
  if (todos.length === 0) {
    return (
      <S.EmptyTimeBlock ref={itemRef} onTouchEnd={onTouchEnd} data-path-id={pathId}>
        <Icon icon="Fire" width={96} height={96} color={colors.green100} />
        <S.EmptyTimeBlockText>나의 시간, 값진 목표로 채워봐요!</S.EmptyTimeBlockText>
      </S.EmptyTimeBlock>
    );
  }

  return (
    <S.TimeBlockList ref={itemRef} onTouchEnd={onTouchEnd} data-path-id={pathId}>
      {todos.map((todo, idx) => (
        <TimeBlockItem
          key={idx}
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
