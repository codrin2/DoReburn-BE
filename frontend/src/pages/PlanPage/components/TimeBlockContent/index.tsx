import { LegacyRef, TouchEvent } from 'react';
import { useNavigate } from 'react-router';

import { DraggingTodo } from './PlanContent';
import * as S from './TimeBlockContent.styled';
import TimeBlockList from './TimeBlockList';

import { Path, PathTodo } from '@/api/plan';
import Icon from '@/components/Icon';
import { getPathColor } from '@/pages/RouteSelectPage/components/RouteResult/RouteList/RouteItem/RouteItem.utils';
import { colors } from '@/styles/theme';

interface TimeBlockProps {
  path: Path;
  draggingTodo: DraggingTodo | null;
  onTouchStart: (e: TouchEvent<HTMLDivElement>, todo: PathTodo) => void;
  onTouchEnd: (e: TouchEvent<HTMLDivElement>) => void;
  onTouchMove: (e: TouchEvent<HTMLDivElement>) => void;
  itemRef?: LegacyRef<HTMLDivElement>;
}

const TimeBlockContent = ({
  path,
  draggingTodo,
  onTouchStart,
  onTouchEnd,
  onTouchMove,
  itemRef,
}: TimeBlockProps) => {
  const navigate = useNavigate();

  const isEmptyTodo = path.todos.length === 0;
  const pathColor = getPathColor(path.trafficType, path.subwayCode);

  const goToRouteTodoEdit = () => {
    navigate(`/plan/${path.pathId}/todos/edit`);
  };

  return (
    <S.TimeBlockContentSection>
      {/* 대중교통 막대바 */}
      <S.TransportBarWrapper>
        <S.TransportBar $pathColor={pathColor} />
      </S.TransportBarWrapper>

      {/* 타임블럭 */}
      <S.TimeBlockContainer>
        <S.TimeBlockWrapper>
          <S.TimeBlockHeader>
            <S.SectionTime>{path.sectionTime}분</S.SectionTime>
            <S.EditButton
              icon={
                <Icon
                  icon={isEmptyTodo ? 'Plus' : 'Edit'}
                  width={16}
                  height={16}
                  color={colors.gray600}
                  cursor="pointer"
                />
              }
              text={isEmptyTodo ? '추가하기' : '수정하기'}
              onClick={goToRouteTodoEdit}
            />
          </S.TimeBlockHeader>
          <TimeBlockList
            todos={path.todos}
            draggingTodo={draggingTodo}
            onTouchStart={onTouchStart}
            onTouchMove={onTouchMove}
            onTouchEnd={onTouchEnd}
            itemRef={itemRef}
          />
        </S.TimeBlockWrapper>
      </S.TimeBlockContainer>
    </S.TimeBlockContentSection>
  );
};

export default TimeBlockContent;
