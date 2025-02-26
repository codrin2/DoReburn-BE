import { LegacyRef, TouchEvent } from 'react';

import * as S from './TimeBlockItem.styled';
import { DraggingTodo } from '../../PlanContent';

import { PathTodo } from '@/api/plan';
import Icon from '@/components/Icon';
import { ICON_MAPPER } from '@/constants/config';
import useCheckTodo from '@/pages/PlanPage/hooks/useCheckTodo';
import useShowTodoBottomSheet from '@/pages/PlanPage/hooks/useShowTodoBottomSheet';

interface TimeBlockItemProps {
  todo: PathTodo;
  onTouchStart: (e: TouchEvent<HTMLDivElement>, todo: PathTodo) => void;
  onTouchMove: (e: TouchEvent<HTMLDivElement>) => void;
  draggingTodo: DraggingTodo | null;
  hasDraggingItem: boolean;
  itemRef?: LegacyRef<HTMLDivElement>;
}

const TimeBlockItem = ({
  todo,
  onTouchStart,
  onTouchMove,
  draggingTodo,
  hasDraggingItem,
  itemRef,
}: TimeBlockItemProps) => {
  const { todoId, category, title, memo, isDone } = todo;
  const isDragging = draggingTodo?.todo.todoId === todoId;

  const { handleCheckTodo, handleUncheckTodo, isAnimatingCheck } = useCheckTodo(
    todo,
    hasDraggingItem,
  );
  const showTodoBottomSheet = useShowTodoBottomSheet();

  const handleViewTodo = () => {
    if (hasDraggingItem) return;

    showTodoBottomSheet(todo);
  };

  return (
    <S.TimeBlockItemLayout
      onTouchStart={(e) => onTouchStart(e, todo)}
      onTouchMove={onTouchMove}
      ref={itemRef}
      $isDragging={isDragging}
      style={isDragging ? { left: `${draggingTodo.x}px`, top: `${draggingTodo.y}px` } : {}}
    >
      <S.CheckIconWrapper
        onClick={isDone ? handleUncheckTodo : handleCheckTodo}
        $isDone={isDone}
        $isAnimating={isAnimatingCheck}
      >
        <Icon icon="EmptyCheck" cursor="pointer" width={28} height={28} />
        <Icon icon="FilledCheck" cursor="pointer" width={28} height={28} />
        <Icon icon={ICON_MAPPER[category]} cursor="pointer" width={28} height={28} />
      </S.CheckIconWrapper>
      <S.TimeBlockContent onClick={handleViewTodo}>
        <S.TodoTitle $isDone={isDone}>{title}</S.TodoTitle>
        <S.TodoMemo>{memo}</S.TodoMemo>
      </S.TimeBlockContent>
    </S.TimeBlockItemLayout>
  );
};

export default TimeBlockItem;
