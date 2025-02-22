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
}

interface PlanContentProps {
  paths?: Path[];
}

const PlanContent = ({ paths }: PlanContentProps) => {
  const touchStartRef = useRef<{ x: number; y: number } | null>(null);
  const [draggingTodo, setDraggingTodo] = useState<DraggingTodo | null>(null);
  const itemRefs = useRef<(HTMLElement | null)[]>([]);

  const { mutate: updatePathTodo } = useUpdatePathTodoMutation();

  const handleTouchStart = (e: TouchEvent<HTMLElement>, todo: PathTodo) => {
    const touch = e.touches[0];
    touchStartRef.current = { x: touch.clientX, y: touch.clientY };

    setDraggingTodo({ todo, x: touch.clientX, y: touch.clientY });
  };

  const handleTouchEnd = (e: React.TouchEvent<HTMLElement>) => {
    const touch = e.changedTouches[0];

    for (const itemRef of itemRefs.current) {
      if (itemRef) {
        const rect = itemRef.getBoundingClientRect();

        const newPathId = Number(itemRef.dataset.pathId);

        if (
          touch.clientY >= rect.top &&
          touch.clientY <= rect.bottom &&
          draggingTodo &&
          newPathId
        ) {
          updatePathTodo({ todoId: draggingTodo.todo.todoId, newPathId });
        }
      }
    }

    setDraggingTodo(null); // 드래그 종료
  };

  const handleTouchMove = (e: React.TouchEvent<HTMLDivElement>) => {
    if (!draggingTodo) return;

    const touch = e.touches[0];
    setDraggingTodo((prev) => (prev ? { ...prev, x: touch.clientX, y: touch.clientY } : null));
  };

  return (
    <S.PlanContent>
      {paths?.map((path, idx) => (
        <S.TimeBlockSection
          key={path.pathId}
          ref={(el) => (itemRefs.current[idx] = el)}
          onTouchEnd={handleTouchEnd}
          data-path-id={path.pathId}
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
