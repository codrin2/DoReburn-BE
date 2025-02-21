import { LegacyRef, TouchEvent } from 'react';

import TimeBlockItem from './TimeBlockItem';
import {
  CheckIconWrapper,
  DraggingItem,
  TimeBlockContent,
  TimeBlockItemLayout,
  TodoMemo,
  TodoTitle,
} from './TimeBlockItem/TimeBlockItem.styled';
import * as S from './TimeBlockList.styled';
import { DraggingTodo } from '../PlanContent';

import { PathTodo } from '@/api/plan';
import Icon from '@/components/Icon';
import { ICON_MAPPER } from '@/constants/config';
import { colors } from '@/styles/theme';

interface TimeBlockListProps {
  todos: PathTodo[];
  draggingTodo: DraggingTodo | null;
  onTouchStart: (e: TouchEvent<HTMLDivElement>, todo: PathTodo) => void;
  onTouchEnd: (e: TouchEvent<HTMLDivElement>) => void;
  onTouchMove: (e: TouchEvent<HTMLDivElement>) => void;
  itemRef?: LegacyRef<HTMLDivElement>;
}

const TimeBlockList = ({
  todos,
  draggingTodo,
  onTouchStart,
  onTouchEnd,
  onTouchMove,
  itemRef,
}: TimeBlockListProps) => {
  // const touchStartRef = useRef<{ x: number; y: number } | null>(null);
  // const [draggingTodo, setDraggingTodo] = useState<{ todo: PathTodo; x: number; y: number } | null>(
  //   null,
  // );
  // const itemRefs = useRef<(HTMLDivElement | null)[]>([]);

  // const handleTouchStart = (e: TouchEvent<HTMLDivElement>, todo: PathTodo) => {
  //   const touch = e.touches[0];
  //   touchStartRef.current = { x: touch.clientX, y: touch.clientY };
  //   console.log('handleTouchStart:', touchStartRef.current, todo);
  //   setDraggingTodo({ todo, x: touch.clientX, y: touch.clientY });
  // };

  // // ✅ 터치 종료 → 특정 `TimeBlockItem` 위에 놓이면 API 호출
  // const handleTouchEnd = (e: React.TouchEvent<HTMLDivElement>) => {
  //   const touch = e.changedTouches[0];
  //   console.log(itemRefs.current);
  //   for (const itemRef of itemRefs.current) {
  //     if (itemRef) {
  //       const rect = itemRef.getBoundingClientRect();
  //       console.log('touch:', touch.clientX, touch.clientY);
  //       console.log('rect:', rect.top, rect.bottom);

  //       if (touch.clientY >= rect.top && touch.clientY <= rect.bottom) {
  //         // const newTodoId = Number(itemRef.dataset.todoId);
  //         console.log(rect);

  //         // moveTodoToNewItem(touchStartTodoIdRef.current, newTodoId);
  //         break;
  //       }
  //     }
  //   }

  //   setDraggingTodo(null); // 드래그 종료
  // };

  // const handleTouchMove = (e: React.TouchEvent<HTMLDivElement>) => {
  //   if (!draggingTodo) return;

  //   const touch = e.touches[0];
  //   setDraggingTodo((prev) => (prev ? { ...prev, x: touch.clientX, y: touch.clientY } : null));
  // };

  if (todos.length === 0) {
    return (
      <S.EmptyTimeBlock>
        <Icon icon="Fire" width={96} height={96} color={colors.green100} />
        <S.EmptyTimeBlockText>나의 시간, 값진 목표로 채워봐요!</S.EmptyTimeBlockText>
      </S.EmptyTimeBlock>
    );
  }

  return (
    <S.TimeBlockList ref={itemRef}>
      {todos.map((todo, idx) => (
        <TimeBlockItem
          key={idx}
          todo={todo}
          onTouchStart={onTouchStart}
          onTouchEnd={onTouchEnd}
          onTouchMove={onTouchMove}
        />
      ))}

      {/* ✅ 드래그 중인 요소를 따라다니는 UI */}
      {draggingTodo && (
        <DraggingItem
          style={{
            left: draggingTodo.x,
            top: draggingTodo.y,
          }}
        >
          <TimeBlockItemLayout>
            <CheckIconWrapper $isDone={draggingTodo.todo.isDone}>
              <Icon icon="EmptyCheck" cursor="pointer" />
              <Icon icon="FilledCheck" cursor="pointer" />
              <Icon icon={ICON_MAPPER[draggingTodo.todo.category]} cursor="pointer" />
            </CheckIconWrapper>
            <TimeBlockContent>
              <TodoTitle $isDone={draggingTodo.todo.isDone}>{draggingTodo.todo.title}</TodoTitle>
              <TodoMemo>{draggingTodo.todo.memo}</TodoMemo>
            </TimeBlockContent>
          </TimeBlockItemLayout>
        </DraggingItem>
      )}
    </S.TimeBlockList>
  );
};

export default TimeBlockList;
