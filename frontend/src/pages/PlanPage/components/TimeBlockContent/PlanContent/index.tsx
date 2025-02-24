import { TouchEvent, useRef, useState } from 'react';

import TimeBlockContent from '..';
import * as S from './PlanContent.styled';
import TimeBlockHeader from '../../TimeBlockHeader';

import { Path, PathTodo } from '@/api/plan';
import { ERROR_MESSAGE } from '@/constants/message';
import useToast from '@/hooks/useToast';
import useUpdatePathTodoMutation from '@/pages/PlanPage/hooks/useUpdatePathTodoMutation';

const LONG_PRESS_DURATION = 500;

export interface DraggingTodo {
  todo: PathTodo;
  x: number;
  y: number;
  offsetX: number;
  offsetY: number;
}

interface PlanContentProps {
  paths?: Path[];
}

const PlanContent = ({ paths }: PlanContentProps) => {
  const [draggingTodo, setDraggingTodo] = useState<DraggingTodo | null>(null);
  const timeBlockRefs = useRef(new Map<number, HTMLElement>());
  const longPressTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const isDraggingRef = useRef(false);

  const { mutateAsync: updatePathTodo } = useUpdatePathTodoMutation();
  const { toast } = useToast();

  const handleTouchStart = (e: TouchEvent<HTMLElement>, todo: PathTodo) => {
    isDraggingRef.current = false;
    const touch = e.touches[0];
    const targetElement = e.currentTarget;

    // 터치한 위치와 block의 위치 차이 계산하여 보정
    const offsetX = touch.pageX - targetElement.offsetLeft;
    const offsetY = touch.pageY - targetElement.offsetTop;

    longPressTimeoutRef.current = setTimeout(() => {
      isDraggingRef.current = true;
      setDraggingTodo({
        todo,
        x: touch.pageX - offsetX,
        y: touch.pageY - offsetY,
        offsetX,
        offsetY,
      });
    }, LONG_PRESS_DURATION);
  };

  const handleTouchMove = (e: React.TouchEvent<HTMLDivElement>) => {
    if (!isDraggingRef.current) {
      clearTimeout(longPressTimeoutRef.current!); // 스크롤 시 터치 취소

      return;
    }

    if (!draggingTodo) return;

    const touch = e.touches[0];

    setDraggingTodo((prev) =>
      prev
        ? {
            ...prev,
            x: touch.pageX - prev.offsetX,
            y: touch.pageY - prev.offsetY,
          }
        : null,
    );
  };

  const handleTouchEnd = async (e: React.TouchEvent<HTMLElement>) => {
    if (longPressTimeoutRef.current) {
      clearTimeout(longPressTimeoutRef.current);
    }

    if (!draggingTodo) return;

    e.preventDefault();

    const touch = e.changedTouches[0];

    for (const [pathId, timeBlockRef] of timeBlockRefs.current) {
      const rect = timeBlockRef.getBoundingClientRect();
      const isValidTargetBlock = touch.clientY >= rect.top && touch.clientY <= rect.bottom;

      if (isValidTargetBlock) {
        await updatePathTodo(
          { todoId: draggingTodo.todo.todoId, newPathId: pathId },
          {
            onError: () => {
              toast({ message: ERROR_MESSAGE.updatePathTodo });
            },
          },
        );

        break;
      }
    }

    setDraggingTodo(null);
  };

  return (
    <S.PlanContent>
      {paths?.map((path) => (
        <S.TimeBlockSection
          key={path.pathId}
          ref={(node) => {
            if (node && timeBlockRefs.current.get(path.pathId) !== node) {
              timeBlockRefs.current.set(path.pathId, node);
            } else if (node === null) {
              timeBlockRefs.current.delete(path.pathId);
            }
          }}
          onTouchEnd={handleTouchEnd}
        >
          <TimeBlockHeader path={path} />
          <TimeBlockContent
            path={path}
            draggingTodo={draggingTodo}
            onTouchStart={handleTouchStart}
            onTouchMove={handleTouchMove}
          />
        </S.TimeBlockSection>
      ))}
    </S.PlanContent>
  );
};

export default PlanContent;
