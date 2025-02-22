import { TouchEvent, useRef, useState } from 'react';

import TimeBlockContent from '..';
import * as S from './PlanContent.styled';
import TimeBlockHeader from '../../TimeBlockHeader';

import { Path, PathTodo } from '@/api/plan';
import { ERROR_MESSAGE } from '@/constants/message';
import useToast from '@/hooks/useToast';
import useUpdatePathTodoMutation from '@/pages/PlanPage/hooks/useUpdatePathTodoMutation';

const LONG_PRESS_DURATION = 300;

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

  const { mutateAsync: updatePathTodo } = useUpdatePathTodoMutation();
  const { toast } = useToast();

  const handleTouchStart = (e: TouchEvent<HTMLElement>, todo: PathTodo) => {
    const touch = e.touches[0];
    const targetBlock = (e.target as HTMLElement).getBoundingClientRect();

    // 터치한 위치와 block의 위치 차이 계산하여 보정
    const offsetX = touch.clientX - targetBlock.left;
    const offsetY = touch.clientY - targetBlock.top;

    longPressTimeoutRef.current = setTimeout(() => {
      setDraggingTodo({
        todo,
        x: touch.clientX - offsetX,
        y: touch.clientY - offsetY,
        offsetX,
        offsetY,
      });
    }, LONG_PRESS_DURATION);
  };

  const handleTouchMove = (e: React.TouchEvent<HTMLDivElement>) => {
    if (!draggingTodo) return;

    const touch = e.touches[0];
    setDraggingTodo((prev) =>
      prev
        ? {
            ...prev,
            x: touch.clientX - prev.offsetX,
            y: touch.clientY - prev.offsetY,
          }
        : null,
    );
  };

  const handleTouchEnd = async (e: React.TouchEvent<HTMLElement>) => {
    if (longPressTimeoutRef.current) {
      clearTimeout(longPressTimeoutRef.current);
    }

    if (!draggingTodo) return;

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
          <TimeBlockHeader
            trafficType={path.trafficType}
            startStation={path.startName}
            subwayCode={path.subwayCode}
            busNumber={path.busNumber}
          />
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
