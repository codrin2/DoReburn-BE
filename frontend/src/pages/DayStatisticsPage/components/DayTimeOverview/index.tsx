import * as S from './DayTimeOverview.styled';

interface DayTimeOverviewProps {
  moveTime: number;
  usageTime: number;
  isMoved: boolean;
}

const DayTimeOverview = ({ moveTime, usageTime, isMoved }: DayTimeOverviewProps) => {
  return (
    <S.DayTimeOverviewContainer $isMoved={isMoved}>
      <S.TimeBox>
        <S.TimeTitle>총 이동 시간</S.TimeTitle>
        <S.TimeValue>{moveTime}분</S.TimeValue>
      </S.TimeBox>
      <S.Divider />
      <S.TimeBox>
        <S.TimeTitle>총 활용 시간</S.TimeTitle>
        <S.TimeValue>{usageTime}분</S.TimeValue>
      </S.TimeBox>
    </S.DayTimeOverviewContainer>
  );
};

export default DayTimeOverview;
