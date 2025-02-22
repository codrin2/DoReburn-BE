import * as S from './DayTimeOverview.styled';

interface DayTimeOverviewProps {
  usageTime: number;
  todoCount: number;
  isMoved: boolean;
}

const DayTimeOverview = ({ usageTime, todoCount, isMoved }: DayTimeOverviewProps) => {
  return (
    <S.DayTimeOverviewContainer $isMoved={isMoved}>
      <S.TimeBox>
        <S.TimeTitle>총 활용 시간</S.TimeTitle>
        <S.TimeValue>{usageTime}분</S.TimeValue>
      </S.TimeBox>
      <S.Divider />
      <S.TimeBox>
        <S.TimeTitle>총 한 일 개수</S.TimeTitle>
        <S.TimeValue>{todoCount}개</S.TimeValue>
      </S.TimeBox>
    </S.DayTimeOverviewContainer>
  );
};

export default DayTimeOverview;
