import { useNavigate } from 'react-router';

import * as S from './TimeBlockContent.styled';
import TimeBlockList from './TimeBlockList';
import { TrafficType } from '../../PlanPage.types';

import { PathTodo } from '@/api/plan';
import Icon from '@/components/Icon';
import { getPathColor } from '@/pages/RouteSelectPage/components/RouteResult/RouteList/RouteItem/RouteItem.utils';
import { colors } from '@/styles/theme';

interface TimeBlockProps {
  pathId: number;
  sectionTime: number;
  todos: PathTodo[];
  trafficType: TrafficType;
  subwayCode: number | null;
}

const TimeBlockContent = ({
  pathId,
  sectionTime,
  todos,
  trafficType,
  subwayCode,
}: TimeBlockProps) => {
  const navigate = useNavigate();

  const isEmptyTodo = todos.length === 0;
  const pathColor = getPathColor(trafficType, subwayCode);

  const goToRouteTodoEdit = () => {
    navigate(`/plan/${pathId}/todos/edit`);
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
            <S.SectionTime>{sectionTime}분</S.SectionTime>
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
          <TimeBlockList todos={todos} />
        </S.TimeBlockWrapper>
      </S.TimeBlockContainer>
    </S.TimeBlockContentSection>
  );
};

export default TimeBlockContent;
