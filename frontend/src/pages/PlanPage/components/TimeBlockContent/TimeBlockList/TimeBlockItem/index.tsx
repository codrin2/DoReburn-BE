import { useMutation } from '@tanstack/react-query';
import { LegacyRef, TouchEvent, useEffect, useReducer, useRef, useState } from 'react';

import * as S from './TimeBlockItem.styled';
import { DraggingTodo } from '../../PlanContent';

import { checkTodo, PathTodo } from '@/api/plan';
import Icon from '@/components/Icon';
import { ICON_MAPPER } from '@/constants/config';
import useShowTodoBottomSheet from '@/pages/PlanPage/hooks/useShowTodoBottomSheet';

const TODO_CHECK_DELAY = 500;

const useCheckTodoMutation = () => {
  return useMutation({
    mutationFn: ({ todoId, isCompleted }: { todoId: number; isCompleted: boolean }) =>
      checkTodo(todoId, isCompleted),
  });
};

interface TimeBlockItemProps {
  todo: PathTodo;
  onTouchStart: (e: TouchEvent<HTMLDivElement>, todo: PathTodo) => void;
  onTouchMove: (e: TouchEvent<HTMLDivElement>) => void;
  draggingTodo: DraggingTodo | null;
  itemRef?: LegacyRef<HTMLDivElement>;
}

const TimeBlockItem = ({
  todo,
  onTouchStart,
  onTouchMove,
  draggingTodo,
  itemRef,
}: TimeBlockItemProps) => {
  const showTodoBottomSheet = useShowTodoBottomSheet();
  const { mutateAsync: checkTodo } = useCheckTodoMutation();

  const [isDone, toggle] = useReducer((prev) => !prev, todo.isDone);
  const [isAnimating, setIsAnimating] = useState(false);
  const checkTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  const isDragging = draggingTodo?.todo.todoId === todo.todoId;

  const handleUncheckTodo = async () => {
    await checkTodo({ todoId: todo.todoId, isCompleted: false });
    toggle();
  };

  const handleCheckTodo = async () => {
    await checkTodo({ todoId: todo.todoId, isCompleted: !isDone });
    setIsAnimating(true);
    checkTimeoutRef.current = setTimeout(() => {
      toggle();
      setIsAnimating(false);
    }, TODO_CHECK_DELAY);
  };

  useEffect(() => {
    return () => {
      if (checkTimeoutRef.current) {
        clearTimeout(checkTimeoutRef.current);
      }
    };
  }, []);

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
        $isAnimating={isAnimating}
      >
        <Icon icon="EmptyCheck" cursor="pointer" width={28} height={28} />
        <Icon icon="FilledCheck" cursor="pointer" width={28} height={28} />
        <Icon icon={ICON_MAPPER[todo.category]} cursor="pointer" width={28} height={28} />
      </S.CheckIconWrapper>
      <S.TimeBlockContent onClick={() => showTodoBottomSheet(todo)}>
        <S.TodoTitle $isDone={isDone}>{todo.title}</S.TodoTitle>
        <S.TodoMemo>{todo.memo}</S.TodoMemo>
      </S.TimeBlockContent>
    </S.TimeBlockItemLayout>
  );
};

export default TimeBlockItem;
