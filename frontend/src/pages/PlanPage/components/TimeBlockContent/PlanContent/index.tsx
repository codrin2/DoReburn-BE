import { TouchEvent, useRef, useState } from 'react';

import TimeBlockContent from '..';
import * as S from './PlanContent.styled';
import TimeBlockHeader from '../../TimeBlockHeader';

import { Path, PathTodo } from '@/api/plan';
import useUpdatePathTodoMutation from '@/pages/PlanPage/hooks/useUpdatePathTodoMutation';

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
  const touchStartRef = useRef<{ x: number; y: number } | null>(null);
  const [draggingTodo, setDraggingTodo] = useState<DraggingTodo | null>(null);
  const timeBlockRefs = useRef(new Map<number, HTMLElement>());

  const { mutate: updatePathTodo } = useUpdatePathTodoMutation();

  const handleTouchStart = (e: TouchEvent<HTMLElement>, todo: PathTodo) => {
    const touch = e.touches[0];
    const targetBlock = (e.target as HTMLElement).getBoundingClientRect();
    touchStartRef.current = { x: touch.clientX, y: touch.clientY };

    // 터치한 위치와 block의 위치 차이 계산하여 보정
    const offsetX = touch.clientX - targetBlock.left;
    const offsetY = touch.clientY - targetBlock.top;

    setDraggingTodo({
      todo,
      x: touch.clientX - offsetX,
      y: touch.clientY - offsetY,
      offsetX,
      offsetY,
    });
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

  const handleTouchEnd = (e: React.TouchEvent<HTMLElement>) => {
    if (!draggingTodo) return;

    const touch = e.changedTouches[0];

    for (const [pathId, itemRef] of timeBlockRefs.current) {
      const rect = itemRef.getBoundingClientRect();

      if (touch.clientY >= rect.top && touch.clientY <= rect.bottom) {
        updatePathTodo({ todoId: draggingTodo.todo.todoId, newPathId: pathId });
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
          <TimeBlockHeader trafficType={path.trafficType} subwayCode={path.subwayCode} />
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
